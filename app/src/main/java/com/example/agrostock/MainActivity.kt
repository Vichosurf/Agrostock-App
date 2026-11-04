package com.example.agrostock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.screens.CatalogoScreen
import com.example.agrostock.ui.screens.LoginPinScreen
import com.example.agrostock.ui.screens.RetiroDiioScreen
import com.example.agrostock.ui.screens.SyncQueueScreen
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgroOfflineYellow
import com.example.agrostock.ui.theme.AgrostockTheme

enum class AgroScreen(val label: String) {
    LOGIN("1. PIN Acceso"),
    RETIRO("2. Retiro DIIO"),
    CATALOGO("3. Catálogo"),
    COLA_SYNC("4. Cola Envíos")
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Top Demo Navigator Bar allowing direct inspection of all 4 mockups
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E293B),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 28.dp, bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AgroStock - Maquetas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(start = 6.dp)
                        )

                        // Online / Offline Switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
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

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4 Screen Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AgroScreen.entries.forEach { screen ->
                            val isSelected = currentScreen == screen
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { currentScreen = screen },
                                color = if (isSelected) AgroGreenPrimary else Color(0xFF334155),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = screen.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                    )
                                }
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
                AgroScreen.LOGIN -> LoginPinScreen(
                    onLoginSuccess = { currentScreen = AgroScreen.CATALOGO }
                )
                AgroScreen.RETIRO -> RetiroDiioScreen(
                    onBackClick = { currentScreen = AgroScreen.CATALOGO },
                    onSaveMovement = { currentScreen = AgroScreen.COLA_SYNC }
                )
                AgroScreen.CATALOGO -> CatalogoScreen(
                    onSelectProduct = { currentScreen = AgroScreen.RETIRO },
                    onGoToWithdrawal = { currentScreen = AgroScreen.RETIRO },
                    onGoToMovements = { currentScreen = AgroScreen.COLA_SYNC }
                )
                AgroScreen.COLA_SYNC -> SyncQueueScreen(
                    onBackClick = { currentScreen = AgroScreen.CATALOGO },
                    onReturnToCatalog = { currentScreen = AgroScreen.CATALOGO },
                    isOnline = isOnlineMode
                )
            }
        }
    }
}
