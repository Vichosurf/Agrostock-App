package com.example.agrostock.data

import android.content.Context
import com.example.agrostock.data.local.AgroStockSQLiteHelper
import com.example.agrostock.data.remote.AgroStockApiService
import com.example.agrostock.ui.screens.OfflineTransaction

data class SyncSummary(
    val total: Int,
    val sent: Int,
    val failed: Int
) {
    val success: Boolean get() = failed == 0
}

/**
 * Single access point for local data. Every withdrawal is validated and committed
 * locally (stock + movement + outbox) before any network request is attempted.
 */
class AgroStockRepository(context: Context) {
    private val dbHelper = AgroStockSQLiteHelper(context.applicationContext)
    private val apiService = AgroStockApiService()

    fun registerAccount(rut: String, pin: String): Boolean {
        if (dbHelper.usuarioExists(rut)) return false
        return dbHelper.insertUsuario(UserAccount(rut = rut, pin = pin))
    }

    fun authenticateUser(rut: String, pin: String): Boolean =
        dbHelper.authenticateUsuario(rut, pin)

    fun fetchProducts(): List<InventoryProduct> = dbHelper.fetchProducts()

    fun addWithdrawal(
        rut: String,
        productId: Long,
        quantity: Int,
        diio: String?
    ): OfflineTransaction {
        dbHelper.insertWithdrawal(rut, productId, quantity, diio)
        return dbHelper.getAllMovimientos().firstOrNull()
            ?: throw IllegalStateException("El movimiento se guardó, pero no fue posible recuperarlo.")
    }

    fun fetchAllWithdrawals(): List<OfflineTransaction> = dbHelper.getAllMovimientos()

    /**
     * Sends one outbox record at a time. An event is marked as processed only after
     * an HTTP 2xx response that echoes the same id_transaccion_local and an accepted state.
     */
    fun syncPendingWithdrawals(isOnline: Boolean, apiBaseUrl: String): SyncSummary {
        if (!isOnline) return SyncSummary(total = 0, sent = 0, failed = 0)
        val pending = dbHelper.getPendingSyncEvents()
        var sent = 0
        var failed = 0

        pending.forEach { event ->
            val acknowledged = try {
                apiService.syncMovement(apiBaseUrl, event.json, event.transactionId)
            } catch (_: Exception) {
                false
            }

            if (acknowledged) {
                dbHelper.markEventSynced(event.transactionId)
                sent++
            } else {
                dbHelper.markEventAttemptFailed(event.transactionId)
                failed++
            }
        }
        return SyncSummary(total = pending.size, sent = sent, failed = failed)
    }
}
