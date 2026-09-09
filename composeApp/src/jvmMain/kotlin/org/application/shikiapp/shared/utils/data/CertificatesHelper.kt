package org.application.shikiapp.shared.utils.data

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

    private val bundleFile by lazy { File(directory, "ca-certificates.crt") }
    private val hostsFile by lazy { File(directory, "cached_hosts.txt") }

    private val hosts: MutableSet<String> by lazy {
        Collections.synchronizedSet(
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
    }

    fun install(urlString: String) {
        if (!urlString.startsWith("https://", ignoreCase = true)) return

        val host = try {
            URI.create(urlString).toURL().host?.lowercase() ?: return
        } catch (_: Exception) {
            return
        }

        if (hosts.contains(host)) return

        synchronized(this) {
            if (hosts.contains(host)) return // double check (но можно и без этого)

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