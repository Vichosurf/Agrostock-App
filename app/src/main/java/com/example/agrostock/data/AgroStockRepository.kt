package com.example.agrostock.data

import android.content.Context
import com.example.agrostock.data.local.AgroStockSQLiteHelper
import com.example.agrostock.data.remote.AgroStockApiService
import com.example.agrostock.ui.screens.OfflineTransaction

/**
 * Single Source of Truth Repository for AgroStock Application.
 * Coordinates native SQLite database persistence and REST API synchronization.
 */
class AgroStockRepository(context: Context) {

    private val dbHelper = AgroStockSQLiteHelper(context)
    private val apiService = AgroStockApiService()

    // --- AUTHENTICATION & USERS ---

    fun registerAccount(rut: String, pin: String): Boolean {
        if (dbHelper.usuarioExists(rut)) {
            return false
        }
        val newUser = UserAccount(rut = rut, pin = pin)
        val insertedInDb = dbHelper.insertUsuario(newUser)
        if (insertedInDb) {
            apiService.registerUserAccount(newUser)
        }
        return insertedInDb
    }

    fun authenticateUser(rut: String, pin: String): Boolean {
        return dbHelper.authenticateUsuario(rut, pin)
    }

    // --- WITHDRAWAL TRANSACTIONS & SYNC ---

    fun addWithdrawal(title: String, diio: String?): OfflineTransaction {
        val count = dbHelper.getAllMovimientos().size
        val newId = "TX-2024-00${82 + count}"
        val transaction = OfflineTransaction(
            id = newId,
            title = title,
            timeLocation = "Hace un momento · Módulo Fundo",
            diio = diio,
            isSynced = false
        )
        dbHelper.insertMovimiento(transaction, cantidad = 1, requiereDiio = !diio.isNullOrEmpty())
        return transaction
    }

    fun fetchAllWithdrawals(): List<OfflineTransaction> {
        return dbHelper.getAllMovimientos()
    }

    fun syncPendingWithdrawals(isOnline: Boolean): Boolean {
        if (!isOnline) return false

        val localList = dbHelper.getAllMovimientos()
        val apiResponse = apiService.syncWithdrawalQueue(localList)

        if (apiResponse.success) {
            dbHelper.markAllMovimientosSynced()
            return true
        }
        return false
    }
}
