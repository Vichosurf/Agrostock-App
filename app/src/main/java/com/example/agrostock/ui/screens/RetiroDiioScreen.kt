package com.example.agrostock.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.theme.AgroBackground
import com.example.agrostock.ui.theme.AgroBorder
import com.example.agrostock.ui.theme.AgroDarkHeader
import com.example.agrostock.ui.theme.AgroGreenDark
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroRedControlled
import com.example.agrostock.ui.theme.AgroRedLight
import com.example.agrostock.ui.theme.AgroTextPrimary
import com.example.agrostock.ui.theme.AgroTextSecondary

/**
 * Screen 2: Registro de Retiro y DIIO (Módulo Sanitario)
 * Matching Mockup 2 from Image 2 with dynamic 9-digit DIIO entry
 */
@Composable
fun RetiroDiioScreen(
    productName: String = "Penicilina Retard 100ml",
    categoryName: String = "Antibiótico Bovino Inyectable",
    stockText: String = "Stock actual: 24 frascos",
    isControlled: Boolean = true,
    onBackClick: () -> Unit = {},
    onSaveMovement: (quantity: Int, diio: String, title: String) -> Unit = { _, _, _ -> }
) {
    var quantity by remember { mutableIntStateOf(15) }
    var rawDiioDigits by remember { mutableStateOf("123456789") } // 9 digits raw input
    var animalInfo by remember { mutableStateOf("Vaca 402 (Lactancia Activa)") }

    // Format raw digits into 3.3.3 DIIO format (e.g., 123.456.789)
    val formattedDiio = remember(rawDiioDigits) {
        val clean = rawDiioDigits.filter { it.isDigit() }.take(9)
        when {
            clean.length > 6 -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6)}"
            clean.length > 3 -> "${clean.substring(0, 3)}.${clean.substring(3)}"
            else -> clean
        }
    }

    val isDiioValid = rawDiioDigits.filter { it.isDigit() }.length == 9

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroBackground)
    ) {
        // Top Header Bar (Green Background)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AgroGreenDark
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "MÓDULO SANITARIO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "REGISTRAR RETIRO",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    // Badge: DIIO ACTIVO
                    Surface(
                        color = Color(0xFF388E3C),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "DIIO ACTIVO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // CARD 1: PRODUCTO SELECCIONADO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "PRODUCTO SELECCIONADO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (isControlled) {
                                // Red Controlled Tag
                                Surface(
                                    color = AgroRedLight,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "FÁRMACO CONTROLADO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AgroRedControlled,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = productName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = AgroTextPrimary
                            )
                            Text(
                                text = categoryName,
                                fontSize = 12.sp,
                                color = AgroTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stockText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroTextPrimary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            // CARD 2: CANTIDAD A RETIRAR (DOSIS/UNIDADES)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CANTIDAD A RETIRAR (DOSIS/UNIDADES)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quantity Display Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(50.dp),
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$quantity",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AgroTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "UDS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6 Buttons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DeltaButton("-10", Modifier.weight(1f)) { quantity = maxOf(1, quantity - 10) }
                        DeltaButton("-5", Modifier.weight(1f)) { quantity = maxOf(1, quantity - 5) }
                        DeltaButton("-1", Modifier.weight(1f)) { quantity = maxOf(1, quantity - 1) }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DeltaButton("+1", Modifier.weight(1f), isPositive = true) { quantity += 1 }
                        DeltaButton("+5", Modifier.weight(1f), isPositive = true) { quantity += 5 }
                        DeltaButton("+10", Modifier.weight(1f), isPositive = true) { quantity += 10 }
                    }
                }
            }

            // CARD 3: ARETE ANIMAL (DIIO OBLIGATORIO - MÁXIMO 9 DÍGITOS)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARETE ANIMAL (DIIO OBLIGATORIO)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgroRedControlled,
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "SAG Chile",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Editable DIIO Input Field (Maximum 9 Digits)
                    OutlinedTextField(
                        value = rawDiioDigits,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 9) {
                                rawDiioDigits = clean
                            }
                        },
                        label = { Text("Ingresar DIIO (Máx 9 dígitos)", fontSize = 11.sp) },
                        placeholder = { Text("Ej: 123456789", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AgroGreenPrimary,
                            unfocusedBorderColor = AgroBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Linked Crotal Display Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgroGreenPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CROTAL VINCULADO:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroGreenPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (formattedDiio.isBlank()) "DIIO: ---.---.---" else "DIIO: $formattedDiio",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AgroTextPrimary
                                )
                                if (isDiioValid) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = AgroGreenPrimary
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🐄 $animalInfo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scanner Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                // Simulate scanning next ear tag with 9 digits
                                rawDiioDigits = "123456790"
                                animalInfo = "Vaca 405 (Secado - Potrero 2)"
                            },
                        color = AgroDarkHeader,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ESCANEAR CROTAL / CÓDIGO QR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Save Movement Button
            Button(
                onClick = {
                    val movementTitle = "$quantity UDS $productName"
                    onSaveMovement(quantity, formattedDiio, movementTitle)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgroGreenDark)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GUARDAR MOVIMIENTO",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeltaButton(
    text: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isPositive) AgroGreenPrimary else Color(0xFF616161)
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
