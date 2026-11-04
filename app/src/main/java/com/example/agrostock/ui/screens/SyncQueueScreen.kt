package com.example.agrostock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.theme.AgroBackground
import com.example.agrostock.ui.theme.AgroBorder
import com.example.agrostock.ui.theme.AgroDarkHeader
import com.example.agrostock.ui.theme.AgroGreenDark
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgroOfflineYellow
import com.example.agrostock.ui.theme.AgroTextPrimary
import com.example.agrostock.ui.theme.AgroTextSecondary

data class OfflineTransaction(
    val id: String,
    val title: String,
    val detail: String = "",
    val timeLocation: String = "Hace un momento · Módulo Fundo",
    val diio: String? = null,
    val isSynced: Boolean = false
)

/**
 * Screen 4: Cola de Sincronización Offline & Online Sync
 * Matching Mockup 4 from Image 2
 */
@Composable
fun SyncQueueScreen(
    transactions: List<OfflineTransaction> = emptyList(),
    isOnline: Boolean = false,
    onForceSync: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onReturnToCatalog: () -> Unit = {}
) {
    val pendingCount = transactions.count { !it.isSynced }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroBackground)
    ) {
        // Dark Navy Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AgroDarkHeader
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "COLA DE ENVÍOS",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFFFB74D)
                    )
                }
            }
        }

        // Network Status Banner
        val bannerBg = if (isOnline) Color(0xFFE8F5E9) else AgroOfflineYellow
        val bannerBorder = if (isOnline) AgroGreenPrimary else Color(0xFFFFB74D)
        val bannerTextColor = if (isOnline) AgroGreenPrimary else AgroOfflineOrange

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = bannerBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "Status",
                        tint = bannerTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "● MODO ONLINE (CONECTADO - $pendingCount PENDIENTES)" else "MODO OFFLINE ($pendingCount PENDIENTES)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = bannerTextColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = bannerTextColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isOnline) "Wi-Fi" else "SQLite",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isOnline)
                        "Conexión a red activa. Puedes forzar la sincronización para enviar los registros pendientes al servidor."
                    else
                        "Transacciones retenidas en local. Los datos se guardan de forma segura en el dispositivo sin riesgo de pérdida.",
                    fontSize = 11.sp,
                    color = AgroTextPrimary,
                    lineHeight = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LISTA DE TRANSACCIONES RETENIDAS EN LOCAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgroTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Total: ${transactions.size} registros",
                    fontSize = 10.sp,
                    color = if (pendingCount > 0) AgroOfflineOrange else AgroGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction Cards List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { tx ->
                    TransactionCardItem(tx = tx)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Force Sync Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable(enabled = isOnline && pendingCount > 0) { onForceSync() },
                color = if (isOnline && pendingCount > 0) AgroGreenPrimary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = if (isOnline && pendingCount > 0) Color.White else Color(0xFF9E9E9E),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (pendingCount == 0) "TODOS LOS REGISTROS SINCRONIZADOS" else "FORZAR SINCRONIZACIÓN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline && pendingCount > 0) Color.White else Color(0xFF757575)
                    )
                }
            }
            Text(
                text = when {
                    pendingCount == 0 -> "Todos los datos han sido transmitidos con éxito"
                    isOnline -> "Presiona para enviar los $pendingCount registros al servidor"
                    else -> "(deshabilitado en offline - esperando conexión Wi-Fi)"
                },
                fontSize = 10.sp,
                color = AgroTextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Volver a Registrar Insumo Button
            Button(
                onClick = onReturnToCatalog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgroGreenDark)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Volver a Registrar Insumo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Footer Bar: Storage & Battery Status
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFECEFF1)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SdCard,
                    contentDescription = "Storage",
                    tint = AgroTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Almacenamiento local SQLite: 12 KB usados",
                    fontSize = 10.sp,
                    color = AgroTextSecondary
                )
                Text(text = "  ·  ", fontSize = 10.sp, color = AgroTextSecondary)
                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Battery",
                    tint = AgroTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Batería: 82%",
                    fontSize = 10.sp,
                    color = AgroTextSecondary
                )
            }
        }
    }
}

@Composable
private fun TransactionCardItem(tx: OfflineTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tx.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AgroTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (tx.isSynced) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                        contentDescription = "Status",
                        tint = if (tx.isSynced) AgroGreenPrimary else AgroOfflineOrange,
                        modifier = Modifier.size(14.dp)
                    )
                }

                if (!tx.diio.isNullOrEmpty()) {
                    Text(
                        text = tx.diio,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = tx.timeLocation,
                    fontSize = 11.sp,
                    color = AgroTextSecondary
                )
                Text(
                    text = "ID: ${tx.id}",
                    fontSize = 9.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            // Sync Status Badge
            val badgeBg = if (tx.isSynced) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            val badgeBorder = if (tx.isSynced) AgroGreenPrimary else Color(0xFFFFCC80)
            val badgeTextColor = if (tx.isSynced) AgroGreenPrimary else AgroOfflineOrange

            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, badgeBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (tx.isSynced) "SINCRONIZADO" else "SYNC_PENDING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeTextColor
                    )
                    Text(
                        text = if (tx.isSynced) "Enviado a nube" else "Esperando red",
                        fontSize = 8.sp,
                        color = AgroTextSecondary
                    )
                }
            }
        }
    }
}
