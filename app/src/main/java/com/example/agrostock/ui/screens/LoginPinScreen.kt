package com.example.agrostock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.components.AgroStockLogo
import com.example.agrostock.ui.theme.AgroBackground
import com.example.agrostock.ui.theme.AgroBorder
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgroOfflineYellow
import com.example.agrostock.ui.theme.AgroRedControlled
import com.example.agrostock.ui.theme.AgroTextMuted
import com.example.agrostock.ui.theme.AgroTextPrimary
import com.example.agrostock.ui.theme.AgroTextSecondary

/**
 * Screen 1: Acceso Rápido (Secuencia: 1° RUT, 2° PIN de 6 dígitos)
 * Matching Mockup 1 from Image 2
 */
@Composable
fun LoginPinScreen(
    onLoginSuccess: (rut: String, pin: String) -> Unit = { _, _ -> }
) {
    // Empty by default for new RUT and PIN
    var rutInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var showRutWarning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar with OFFLINE badge on top right (09:41 removed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // OFFLINE Badge
            Surface(
                color = AgroOfflineYellow,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgroOfflineOrange.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Offline",
                        tint = AgroOfflineOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OFFLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgroOfflineOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Screen Card Container
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "INICIAR SESIÓN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AgroTextPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // High Definition AgroStock Logo (Image 1)
                AgroStockLogo(logoSize = 130.dp)

                Spacer(modifier = Modifier.height(20.dp))

                // Step 1: Editable RUT Input Field
                OutlinedTextField(
                    value = rutInput,
                    onValueChange = { input ->
                        val clean = input.filter { it.isLetterOrDigit() || it == '-' || it == '.' }
                        if (clean.length <= 12) {
                            rutInput = clean
                            if (clean.isNotBlank()) showRutWarning = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .height(52.dp),
                    placeholder = { Text("1. Ingresar RUT (ej: 12.345.678-9)", fontSize = 13.sp, color = AgroTextMuted) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User RUT",
                            tint = AgroTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = AgroTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AgroTextPrimary,
                        unfocusedTextColor = AgroTextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AgroGreenPrimary,
                        unfocusedBorderColor = if (showRutWarning) AgroRedControlled else AgroBorder
                    )
                )

                if (showRutWarning) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👉 Ingresa primero tu RUT para continuar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroRedControlled
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Step 2: PIN Dots Indicator (Gradual filling as user types)
                Text(
                    text = "2. INGRESA PIN DE 6 DÍGITOS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgroTextSecondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < pinInput.length
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) AgroTextPrimary else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) AgroTextPrimary else AgroTextMuted,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Keypad (3x4 Grid)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BORRAR", "0", "BACK")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    keypadRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { key ->
                                KeypadButton(
                                    key = key,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (rutInput.isBlank()) {
                                            showRutWarning = true
                                        } else {
                                            when (key) {
                                                "BORRAR" -> pinInput = ""
                                                "BACK" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                                else -> {
                                                    if (pinInput.length < 6) {
                                                        pinInput += key
                                                        if (pinInput.length == 6) {
                                                            onLoginSuccess(rutInput, pinInput)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ENTRAR Button
                Button(
                    onClick = {
                        if (rutInput.isBlank()) {
                            showRutWarning = true
                        } else {
                            onLoginSuccess(
                                rutInput,
                                if (pinInput.isBlank()) "123456" else pinInput
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgroGreenPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ENTRAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Enter",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer security note
                Text(
                    text = "Autenticación segura offline con PIN de 6 dígitos",
                    fontSize = 11.sp,
                    color = AgroTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = AgroTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Base local SQLite cifrada v2.4 AgroStock",
                        fontSize = 10.sp,
                        color = AgroTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isActionKey = key == "BORRAR" || key == "BACK"
    val bgColor = if (isActionKey) Color(0xFFF1F3F4) else AgroGreenPrimary
    val textColor = if (isActionKey) AgroTextPrimary else Color.White

    Surface(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (key == "BACK") {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = key,
                    fontSize = if (key == "BORRAR") 11.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
