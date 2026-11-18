package com.example.agrostock.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.agrostock.data.AgroStockRepository
import com.example.agrostock.data.SyncSummary
import com.example.agrostock.data.InventoryProduct
import com.example.agrostock.ui.screens.OfflineTransaction
import com.example.agrostock.ui.screens.ProductItem

/**
 * ViewModel compatible con el Repository offline-first.
 * La UI principal aún mantiene su estado en MainActivity; este ViewModel queda
 * preparado para una migración posterior de navegación/estado.
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

    fun loadProducts(): List<InventoryProduct> = repository.fetchProducts()

    fun authenticateUser(rut: String, pin: String): Boolean =
        repository.authenticateUser(rut, pin)

    fun registerUser(rut: String, pin: String): Boolean =
        repository.registerAccount(rut, pin)

    fun saveWithdrawalMovement(
        rut: String,
        productId: Long,
        quantity: Int,
        diio: String?
    ): OfflineTransaction {
        val movement = repository.addWithdrawal(rut, productId, quantity, diio)
        loadTransactionsFromDb()
        return movement
    }

    fun forceSync(isOnline: Boolean, apiBaseUrl: String): SyncSummary {
        val summary = repository.syncPendingWithdrawals(isOnline, apiBaseUrl)
        if (summary.sent > 0) loadTransactionsFromDb()
        return summary
    }
}
