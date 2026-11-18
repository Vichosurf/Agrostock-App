package com.example.agrostock

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.data.AgroStockRepository
import com.example.agrostock.ui.screens.CatalogoScreen
import com.example.agrostock.ui.screens.LoginPinScreen
import com.example.agrostock.ui.screens.ProductItem
import com.example.agrostock.ui.screens.RetiroDiioScreen
import com.example.agrostock.ui.screens.SyncQueueScreen
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgrostockTheme
import com.example.agrostock.util.NetworkObserver
import java.util.concurrent.Executors

enum class AgroScreen { LOGIN, RETIRO, CATALOGO, COLA_SYNC }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AgrostockTheme { AgroStockMainApp() } }
    }
}

@Composable
fun AgroStockMainApp() {
    val context = LocalContext.current
    val repository = remember { AgroStockRepository(context.applicationContext) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val syncExecutor = remember { Executors.newSingleThreadExecutor() }

    var currentScreen by remember { mutableStateOf(AgroScreen.LOGIN) }
    var isOnlineMode by remember { mutableStateOf(NetworkObserver.isNetworkConnected(context)) }
    var loggedInRut by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }

    fun loadCatalog(): List<ProductItem> = repository.fetchProducts().map { product ->
        val icon = when (product.category) {
            "Forrajes" -> Icons.Default.Grass
            "Sales" -> Icons.Default.Star
            "Fármacos" -> Icons.Default.LocalPharmacy
            else -> Icons.Default.Science
        }
        val actionLabel = when (product.category) {
            "Forrajes" -> "FORRAJE >"
            "Sales" -> "SALES >"
            "Fármacos" -> "FÁRMACOS >"
            else -> "FERTILIZANTES >"
        }
        ProductItem(
            id = product.id.toString(),
            name = product.name,
            category = product.category,
            stock = "Stock: ${product.stock.toInt()} ${product.unit}",
            icon = icon,
            buttonText = actionLabel,
            buttonColor = AgroGreenPrimary,
            productId = product.id,
            stockQuantity = product.stock.toInt(),
            unit = product.unit,
            requiresDiio = product.requiresDiio
        )
    }

    var catalogProducts by remember { mutableStateOf(loadCatalog()) }
    var selectedProductItem by remember { mutableStateOf<ProductItem?>(null) }
    var transactions by remember { mutableStateOf(repository.fetchAllWithdrawals()) }

    DisposableEffect(context) {
        val callback = NetworkObserver.registerNetworkCallback(context) { connected ->
            isOnlineMode = connected
        }
        onDispose {
            if (callback != null) {
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                manager?.unregisterNetworkCallback(callback)
            }
        }
    }

    DisposableEffect(syncExecutor) {
        onDispose { syncExecutor.shutdown() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != AgroScreen.LOGIN) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 12.dp, top = 28.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AgroStock Terreno",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    loggedInRut = ""
                                    currentScreen = AgroScreen.LOGIN
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Cerrar sesión",
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isOnlineMode) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = "Conectividad",
                                tint = if (isOnlineMode) Color(0xFF81C784) else AgroOfflineOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isOnlineMode) "ONLINE" else "OFFLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnlineMode) Color(0xFF81C784) else AgroOfflineOrange
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            when (currentScreen) {
                AgroScreen.LOGIN -> LoginPinScreen(
                    onLoginAttempt = repository::authenticateUser,
                    onRegisterAccount = repository::registerAccount,
                    onLoginSuccess = { rut ->
                        loggedInRut = rut
                        transactions = repository.fetchAllWithdrawals()
                        catalogProducts = loadCatalog()
                        currentScreen = AgroScreen.CATALOGO
                    }
                )

                AgroScreen.CATALOGO -> CatalogoScreen(
                    userRut = loggedInRut,
                    isOnline = isOnlineMode,
                    products = catalogProducts,
                    onSelectProduct = { product ->
                        selectedProductItem = product
                        currentScreen = AgroScreen.RETIRO
                    },
                    onGoToWithdrawal = {
                        selectedProductItem = catalogProducts.firstOrNull()
                        if (selectedProductItem != null) {
                            currentScreen = AgroScreen.RETIRO
                        } else {
                            Toast.makeText(context, "No hay productos disponibles.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onGoToMovements = {
                        transactions = repository.fetchAllWithdrawals()
                        currentScreen = AgroScreen.COLA_SYNC
                    }
                )

                AgroScreen.RETIRO -> {
                    val product = selectedProductItem
                    RetiroDiioScreen(
                        productName = product?.name ?: "Producto no seleccionado",
                        categoryName = product?.category ?: "",
                        stockText = product?.stock ?: "Stock no disponible",
                        maxStock = product?.stockQuantity ?: 0,
                        isControlled = product?.requiresDiio == true,
                        onBackClick = { currentScreen = AgroScreen.CATALOGO },
                        onSaveMovement = { quantity, diio, _ ->
                            try {
                                val selected = product
                                    ?: throw IllegalArgumentException("Selecciona un producto antes de continuar.")
                                repository.addWithdrawal(
                                    rut = loggedInRut,
                                    productId = selected.productId,
                                    quantity = quantity,
                                    diio = diio.takeIf { selected.requiresDiio }
                                )
                                transactions = repository.fetchAllWithdrawals()
                                catalogProducts = loadCatalog()
                                currentScreen = AgroScreen.COLA_SYNC
                            } catch (error: IllegalArgumentException) {
                                Toast.makeText(
                                    context,
                                    error.message ?: "No se pudo registrar el movimiento.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (error: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error local: ${error.message ?: "operación no completada"}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }

                AgroScreen.COLA_SYNC -> SyncQueueScreen(
                    transactions = transactions,
                    isOnline = isOnlineMode,
                    onForceSync = {
                        if (!isOnlineMode) {
                            Toast.makeText(context, "No hay conexión validada.", Toast.LENGTH_SHORT).show()
                        } else if (!isSyncing) {
                            isSyncing = true
                            syncExecutor.execute {
                                val summary = repository.syncPendingWithdrawals(
                                    isOnline = true,
                                    apiBaseUrl = BuildConfig.AGROSTOCK_API_BASE_URL
                                )
                                val refreshed = repository.fetchAllWithdrawals()
                                mainHandler.post {
                                    transactions = refreshed
                                    isSyncing = false
                                    val message = when {
                                        summary.total == 0 -> "No hay movimientos pendientes."
                                        summary.failed == 0 -> "Sincronización confirmada: ${summary.sent} movimientos."
                                        else -> "Enviados: ${summary.sent}. Pendientes con error: ${summary.failed}."
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    onBackClick = { currentScreen = AgroScreen.CATALOGO },
                    onReturnToCatalog = {
                        catalogProducts = loadCatalog()
                        currentScreen = AgroScreen.CATALOGO
                    }
                )
            }
        }
    }
}
