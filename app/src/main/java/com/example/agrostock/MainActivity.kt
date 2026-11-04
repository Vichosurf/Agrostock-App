package com.example.agrostock

import android.os.Bundle
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
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.screens.CatalogoScreen
import com.example.agrostock.ui.screens.LoginPinScreen
import com.example.agrostock.ui.screens.OfflineTransaction
import com.example.agrostock.ui.screens.ProductItem
import com.example.agrostock.ui.screens.RetiroDiioScreen
import com.example.agrostock.ui.screens.SyncQueueScreen
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgroOfflineYellow
import com.example.agrostock.ui.theme.AgrostockTheme

enum class AgroScreen {
    LOGIN,
    RETIRO,
    CATALOGO,
    COLA_SYNC
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgrostockTheme {
                AgroStockMainApp()
            }
        }
    }
}

@Composable
fun AgroStockMainApp() {
    var currentScreen by remember { mutableStateOf(AgroScreen.LOGIN) }
    var isOnlineMode by remember { mutableStateOf(false) }
    var loggedInRut by remember { mutableStateOf("14.821.903-K") }

    // Selected product for withdrawal
    var selectedProductItem by remember {
        mutableStateOf<ProductItem?>(null)
    }

    // Dynamic list of transactions
    val transactions = remember {
        mutableStateListOf(
            OfflineTransaction(
                id = "TX-2024-0081",
                title = "15 sacos de Concentrado",
                timeLocation = "Hace 10 min · Galpón 2"
            ),
            OfflineTransaction(
                id = "TX-2024-0080",
                title = "2 fármacos (Penicilina Bov 100ml)",
                timeLocation = "Hace 25 min · Enfermería",
                diio = "DIIO: 123.456.789"
            ),
            OfflineTransaction(
                id = "TX-2024-0079",
                title = "50 kg Sal Mineral Bloque",
                timeLocation = "Hace 40 min · Potrero 4"
            ),
            OfflineTransaction(
                id = "TX-2024-0078",
                title = "10 sacos Fertilizante Urea",
                timeLocation = "Hace 1 hora · Bodega Central"
            ),
            OfflineTransaction(
                id = "TX-2024-0077",
                title = "1 frasco Ivermectina 500ml",
                timeLocation = "Hace 2 horas · Bodega Vivero"
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Sleek Top Header Bar (ONLY visible when logged in)
            if (currentScreen != AgroScreen.LOGIN) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 28.dp, bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    onClick = { currentScreen = AgroScreen.LOGIN },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar sesión",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Online / Offline Network Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOnlineMode) Icons.Default.Wifi else Icons.Default.WifiOff,
                                    contentDescription = "Mode",
                                    tint = if (isOnlineMode) Color(0xFF4CAF50) else AgroOfflineOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOnlineMode) "ONLINE" else "OFFLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnlineMode) Color(0xFF81C784) else AgroOfflineYellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = isOnlineMode,
                                    onCheckedChange = { isOnlineMode = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AgroGreenPrimary,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = AgroOfflineOrange
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                // Step 1: LOGIN -> Navigates to CATALOGO
                AgroScreen.LOGIN -> LoginPinScreen(
                    onLoginSuccess = { userRut, _ ->
                        if (userRut.isNotBlank()) {
                            loggedInRut = userRut
                        }
                        currentScreen = AgroScreen.CATALOGO
                    }
                )

                // Step 2: CATALOGO -> Selecting product navigates to RETIRO
                AgroScreen.CATALOGO -> CatalogoScreen(
                    userRut = loggedInRut,
                    isOnline = isOnlineMode,
                    onSelectProduct = { product ->
                        selectedProductItem = product
                        currentScreen = AgroScreen.RETIRO
                    },
                    onGoToWithdrawal = {
                        currentScreen = AgroScreen.RETIRO
                    },
                    onGoToMovements = {
                        currentScreen = AgroScreen.COLA_SYNC
                    }
                )

                // Step 3: RETIRO -> Saving movement creates record and navigates to COLA_SYNC
                AgroScreen.RETIRO -> {
                    val rawStockString = selectedProductItem?.stock ?: "24"
                    val parsedStock = rawStockString.filter { it.isDigit() }.toIntOrNull() ?: 24

                    RetiroDiioScreen(
                        productName = selectedProductItem?.name ?: "Saco de Concentrado",
                        categoryName = selectedProductItem?.category ?: "Forrajes",
                        stockText = selectedProductItem?.stock ?: "Stock: 124 sacos",
                        maxStock = parsedStock,
                        isControlled = selectedProductItem?.category == "Fármacos",
                        onBackClick = {
                            currentScreen = AgroScreen.CATALOGO
                        },
                        onSaveMovement = { _, diio, movementTitle ->
                            val newNumber = 82 + transactions.size
                            val newId = "TX-2024-00$newNumber"
                            transactions.add(
                                0,
                                OfflineTransaction(
                                    id = newId,
                                    title = movementTitle,
                                    timeLocation = "Hace un momento · Módulo Fundo",
                                    diio = "DIIO: $diio",
                                    isSynced = false
                                )
                            )
                            currentScreen = AgroScreen.COLA_SYNC
                        }
                    )
                }

                // Step 4: COLA_SYNC -> Displays queue and allows syncing when Online
                AgroScreen.COLA_SYNC -> SyncQueueScreen(
                    transactions = transactions,
                    isOnline = isOnlineMode,
                    onForceSync = {
                        val updatedList = transactions.map { it.copy(isSynced = true) }
                        transactions.clear()
                        transactions.addAll(updatedList)
                    },
                    onBackClick = {
                        currentScreen = AgroScreen.CATALOGO
                    },
                    onReturnToCatalog = {
                        currentScreen = AgroScreen.CATALOGO
                    }
                )
            }
        }
    }
}
