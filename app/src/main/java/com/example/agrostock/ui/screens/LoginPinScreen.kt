package com.example.agrostock.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.agrostock.util.RutUtils

/**
 * Screen 1: Acceso Rápido y Registro de Cuenta (RUT Módulo 11, Email y PIN 6 dígitos)
 * Footer texts removed as requested. Smooth RUT typing fix applied.
 */
@Composable
fun LoginPinScreen(
    onLoginAttempt: (rut: String, pin: String) -> Boolean = { _, _ -> true },
    onRegisterAccount: (rut: String, email: String, pin: String) -> Boolean = { _, _, _ -> true },
    onLoginSuccess: (rut: String) -> Unit = {}
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var rutInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun processLogin() {
        errorMessage = null
        successMessage = null

        val formattedRut = RutUtils.formatRut(rutInput)
        if (!RutUtils.isValidRut(rutInput)) {
            errorMessage = "⚠️ RUT chileno inválido (Revise el dígito verificador)."
            return
        }

        if (pinInput.length < 6) {
            errorMessage = "⚠️ Ingresa el PIN completo de 6 dígitos."
            return
        }

        val isValid = onLoginAttempt(formattedRut, pinInput)
        if (isValid) {
            onLoginSuccess(formattedRut)
        } else {
            errorMessage = "⚠️ RUT o PIN no registrado / incorrecto. Por favor crea una cuenta."
        }
    }

    fun processRegister() {
        errorMessage = null
        successMessage = null

        val formattedRut = RutUtils.formatRut(rutInput)
        if (!RutUtils.isValidRut(rutInput)) {
            errorMessage = "⚠️ RUT chileno inválido según Módulo 11."
            return
        }

        if (emailInput.isBlank() || !emailInput.contains("@")) {
            errorMessage = "⚠️ Ingresa un correo electrónico válido."
            return
        }

        if (pinInput.length < 6) {
            errorMessage = "⚠️ Ingresa un PIN de 6 dígitos para la cuenta."
            return
        }

        val registered = onRegisterAccount(formattedRut, emailInput, pinInput)
        if (registered) {
            successMessage = "✅ ¡Cuenta creada exitosamente! Ya puedes ingresar."
            isRegisterMode = false
            pinInput = ""
        } else {
            errorMessage = "⚠️ Este RUT ya se encuentra registrado."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar with OFFLINE badge on top right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = if (isRegisterMode) "CREAR CUENTA" else "INICIAR SESIÓN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AgroTextPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // High Definition AgroStock Logo (Image 1)
                AgroStockLogo(logoSize = 110.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Status Messages
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroRedControlled,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                if (!successMessage.isNullOrEmpty()) {
                    Text(
                        text = successMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroGreenPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Step 1: RUT Input Field (Smooth Typing without character duplication)
                OutlinedTextField(
                    value = rutInput,
                    onValueChange = { input ->
                        val clean = RutUtils.cleanRut(input)
                        if (clean.length <= 9) {
                            rutInput = if (clean.length >= 8) RutUtils.formatRut(clean) else clean
                            errorMessage = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .height(52.dp),
                    placeholder = { Text("RUT (ej: 12.345.678-5)", fontSize = 13.sp, color = AgroTextMuted) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "RUT",
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
                        unfocusedBorderColor = AgroBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 1.5: Email Field (Only in Register Mode)
                AnimatedVisibility(visible = isRegisterMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.90f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            placeholder = { Text("Correo Electrónico (Mail)", fontSize = 13.sp, color = AgroTextMuted) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = AgroTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = AgroTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AgroTextPrimary,
                                unfocusedTextColor = AgroTextPrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = AgroGreenPrimary,
                                unfocusedBorderColor = AgroBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Step 2: PIN Dots Indicator (Gradual filling as user types)
                Text(
                    text = if (isRegisterMode) "CREAR PIN DE 6 DÍGITOS" else "INGRESA PIN DE 6 DÍGITOS",
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

                Spacer(modifier = Modifier.height(18.dp))

                // Keypad (3x4 Grid)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BORRAR", "0", "BACK")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keypadRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                KeypadButton(
                                    key = key,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        when (key) {
                                            "BORRAR" -> pinInput = ""
                                            "BACK" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                            else -> {
                                                if (pinInput.length < 6) {
                                                    pinInput += key
                                                    if (pinInput.length == 6 && !isRegisterMode) {
                                                        processLogin()
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

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button (ENTRAR or REGISTRAR CUENTA)
                Button(
                    onClick = {
                        if (isRegisterMode) processRegister() else processLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgroGreenPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isRegisterMode) "REGISTRAR CUENTA" else "ENTRAR",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.ArrowForward,
                            contentDescription = "Submit",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle between Login & Register mode
                OutlinedButton(
                    onClick = {
                        isRegisterMode = !isRegisterMode
                        errorMessage = null
                        successMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgroGreenPrimary)
                ) {
                    Text(
                        text = if (isRegisterMode) "← Volver a Iniciar Sesión" else "¿No tienes cuenta? CREAR CUENTA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroGreenPrimary
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
            .height(48.dp)
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
                    fontSize = if (key == "BORRAR") 11.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
