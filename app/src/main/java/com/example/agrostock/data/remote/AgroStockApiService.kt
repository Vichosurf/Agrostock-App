package com.example.agrostock.data.remote

import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction

/**
 * AgroStock REST API Service Interface & Mock Implementation.
 * Matches official JSON payload structure from Informe AgroStock Annex C.3.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class SyncPayload(
    val idTransaccionLocal: String,
    val idProducto: Int,
    val requiereDiio: Boolean,
    val diio: String?,
    val cantidad: Int
)

class AgroStockApiService {

    /**
     * Simulates transmitting unsynced withdrawal transactions to the remote FastAPI backend.
     */
    fun syncWithdrawalQueue(transactions: List<OfflineTransaction>): ApiResponse<Int> {
        val unsynced = transactions.filter { !it.isSynced }
        val payloads = unsynced.map { tx ->
            SyncPayload(
                idTransaccionLocal = tx.id,
                idProducto = 204,
                requiereDiio = !tx.diio.isNullOrEmpty(),
                diio = tx.diio,
                cantidad = 1
            )
        }

        return ApiResponse(
            success = true,
            message = "Servidor AgroStock FastAPI: ${payloads.size} movimientos procesados exitosamente.",
            data = payloads.size
        )
    }

    /**
     * Simulates remote cloud backup for user accounts.
     */
    fun registerUserAccount(account: UserAccount): ApiResponse<String> {
        return ApiResponse(
            success = true,
            message = "Usuario ${account.rut} registrado en servidor central AgroStock.",
            data = account.rut
        )
    }
}
