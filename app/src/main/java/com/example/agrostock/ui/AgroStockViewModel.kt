package com.example.agrostock.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.agrostock.data.AgroStockRepository
import com.example.agrostock.ui.screens.OfflineTransaction
import com.example.agrostock.ui.screens.ProductItem

/**
 * AgroStock ViewModel managing reactive application state backed by SQLite Database & API Repository.
 */
class AgroStockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AgroStockRepository(application.applicationContext)

    var currentScreen = mutableStateOf(com.example.agrostock.AgroScreen.LOGIN)
    var isOnlineMode = mutableStateOf(false)
    var loggedInRut = mutableStateOf("")
    var selectedProductItem = mutableStateOf<ProductItem?>(null)

    val transactions = mutableStateListOf<OfflineTransaction>()

    init {
        loadTransactionsFromDb()
    }

    fun loadTransactionsFromDb() {
        transactions.clear()
        transactions.addAll(repository.fetchAllWithdrawals())
    }

    fun authenticateUser(rut: String, pin: String): Boolean {
        return repository.authenticateUser(rut, pin)
    }

    fun registerUser(rut: String, pin: String): Boolean {
        return repository.registerAccount(rut, pin)
    }

    fun saveWithdrawalMovement(movementTitle: String, diio: String?) {
        val newTx = repository.addWithdrawal(movementTitle, diio)
        transactions.add(0, newTx)
    }

    fun forceSync(isOnline: Boolean) {
        val success = repository.syncPendingWithdrawals(isOnline)
        if (success) {
            loadTransactionsFromDb()
        }
    }
}
