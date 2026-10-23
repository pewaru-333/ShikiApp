package org.application.shikiapp.shared.utils.vlc

import java.io.File
import java.nio.file.Files
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.Base64
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object CertificatesHelper {
    private val factory: TrustManagerFactory = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(null as KeyStore?) }

    private val certificates: Set<X509Certificate> = buildSet {
        for (manager in factory.trustManagers) {
            if (manager is X509TrustManager) {
                addAll(manager.acceptedIssuers)
            }
        }
    }

    val directory: File by lazy {
        val encoder = Base64.getMimeEncoder(64, byteArrayOf(10))

        val directory = Files.createTempDirectory("shiki-vlc-ca-").toFile()
        directory.deleteOnExit()

        val file = File(directory, "ca-certificates.crt")
        file.deleteOnExit()

        file.bufferedWriter().use { writer ->
            for (certificate in certificates) {
                writer.appendLine("-----BEGIN CERTIFICATE-----")
                writer.appendLine(encoder.encodeToString(certificate.encoded))
                writer.appendLine("-----END CERTIFICATE-----")
            }
        }

        directory
    }
}