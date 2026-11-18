package com.example.agrostock.data.remote

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * HTTP client for FastAPI POST /api/movimientos/sync.
 * The API base URL is injected at build time (AGROSTOCK_API_BASE_URL).
 * No event is acknowledged locally unless the server echoes the same transaction ID.
 */
class AgroStockApiService {

    fun syncMovement(
        apiBaseUrl: String,
        payloadJson: String,
        expectedTransactionId: String
    ): Boolean {
        if (apiBaseUrl.isBlank()) return false

        val normalizedBase = apiBaseUrl.trim().trimEnd('/')
        val url = URL("$normalizedBase/api/movimientos/sync")
        val scheme = url.protocol.lowercase()
        val localDevelopmentHost = url.host == "10.0.2.2" || url.host == "localhost" || url.host == "127.0.0.1"
        require(scheme == "https" || (scheme == "http" && localDevelopmentHost)) {
            "La API debe utilizar HTTPS. HTTP solo se permite para desarrollo local."
        }

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 20_000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        return try {
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(payloadJson)
            }

            val status = connection.responseCode
            val responseStream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseText = responseStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299 || responseText.isBlank()) return false

            val response = JSONObject(responseText)
            val returnedId = response.optString("id_transaccion_local")
            val state = response.optString("estado")
            returnedId == expectedTransactionId && (state == "PROCESADO" || state == "YA_PROCESADO")
        } finally {
            connection.disconnect()
        }
    }
}
