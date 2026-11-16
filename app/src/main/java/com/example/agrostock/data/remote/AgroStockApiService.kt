package com.example.agrostock.data.remote

import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction

/**
 * AgroStock REST API Service Interface & Mock Implementation.
 * Handles remote cloud synchronization for user registration, login, and withdrawal queues.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

class AgroStockApiService {

    /**
     * Simulates transmitting unsynced withdrawal transactions to the remote cloud server.
     */
    fun syncWithdrawalQueue(transactions: List<OfflineTransaction>): ApiResponse<Int> {
        val unsyncedCount = transactions.count { !it.isSynced }
        return ApiResponse(
            success = true,
            message = "Sincronización con servidor AgroStock completada. $unsyncedCount registros procesados.",
            data = unsyncedCount
        )
    }

    /**
     * Simulates remote cloud backup for a newly created user account.
     */
    fun registerUserAccount(account: UserAccount): ApiResponse<String> {
        return ApiResponse(
            success = true,
            message = "Usuario registrado correctamente en nube AgroStock.",
            data = account.rut
        )
    }
}
