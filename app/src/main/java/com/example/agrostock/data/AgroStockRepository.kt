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
        if (dbHelper.userExists(rut)) {
            return false
        }
        val newUser = UserAccount(rut = rut, pin = pin)
        val insertedInDb = dbHelper.insertUser(newUser)
        if (insertedInDb) {
            apiService.registerUserAccount(newUser)
        }
        return insertedInDb
    }

    fun authenticateUser(rut: String, pin: String): Boolean {
        return dbHelper.authenticateUser(rut, pin)
    }

    // --- WITHDRAWAL TRANSACTIONS & SYNC ---

    fun addWithdrawal(title: String, diio: String?): OfflineTransaction {
        val count = dbHelper.getAllWithdrawals().size
        val newId = "TX-2024-00${82 + count}"
        val transaction = OfflineTransaction(
            id = newId,
            title = title,
            timeLocation = "Hace un momento · Módulo Fundo",
            diio = diio,
            isSynced = false
        )
        dbHelper.insertWithdrawal(transaction)
        return transaction
    }

    fun fetchAllWithdrawals(): List<OfflineTransaction> {
        return dbHelper.getAllWithdrawals()
    }

    fun syncPendingWithdrawals(isOnline: Boolean): Boolean {
        if (!isOnline) return false

        val localList = dbHelper.getAllWithdrawals()
        val apiResponse = apiService.syncWithdrawalQueue(localList)

        if (apiResponse.success) {
            dbHelper.markAllWithdrawalsSynced()
            return true
        }
        return false
    }
}
