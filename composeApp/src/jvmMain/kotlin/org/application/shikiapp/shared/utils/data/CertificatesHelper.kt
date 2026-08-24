package org.application.shikiapp.shared.utils.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Base64
import java.util.Collections
import javax.net.ssl.*

object CertificatesHelper {
    val directory: File by lazy {
        File(System.getProperty("java.io.tmpdir"), "vlc_app_certs_store").apply {
            if (!exists()) mkdirs()
        }
    }

    private val mutex = Mutex()

    private val bundleFile by lazy { File(directory, "ca-certificates.crt") }
    private val hostsFile by lazy { File(directory, "cached_hosts.txt") }

    private val hosts: MutableSet<String> = Collections.synchronizedSet(
        mutableSetOf<String>().apply {
            if (hostsFile.exists()) {
                hostsFile.forEachLine { line ->
                    val host = line.trim().lowercase()
                    if (host.isNotEmpty()) {
                        add(host)
                    }
                }
            }
        }
    )

    suspend fun installCertificates(urlString: String) = withContext(Dispatchers.IO) {
        if (!urlString.startsWith("https://", ignoreCase = true)) return@withContext

        val host = try {
            URI.create(urlString).toURL().host?.lowercase() ?: return@withContext
        } catch (_: Exception) {
            return@withContext
        }

        if (hosts.contains(host)) return@withContext

        mutex.withLock {
            if (hosts.contains(host)) return@withContext // double check (но можно и без этого)

            try {
                val url = URI.create(urlString).toURL()
                val manager = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                    override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) = Unit
                    override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
                        if (chain.isNullOrEmpty()) return

                        val pemData = buildString {
                            for (cert in chain) {
                                append("-----BEGIN CERTIFICATE-----\n")
                                append(Base64.getMimeEncoder(64, byteArrayOf(10)).encodeToString(cert.encoded))
                                append("\n-----END CERTIFICATE-----\n")
                            }
                        }

                        bundleFile.appendText(pemData)
                    }
                })

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, manager, SecureRandom())

                val conn = url.openConnection() as HttpsURLConnection
                conn.sslSocketFactory = sslContext.socketFactory
                conn.hostnameVerifier = HostnameVerifier { _, _ -> true }
                conn.connectTimeout = 4000
                conn.readTimeout = 4000

                conn.connect()
                conn.disconnect()

                hosts.add(host)
                hostsFile.appendText("$host\n")
            } catch (_: Exception) {

            }
        }
    }
}