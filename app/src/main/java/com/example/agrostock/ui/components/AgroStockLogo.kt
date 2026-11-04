package com.example.agrostock.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroRedControlled
import com.example.agrostock.ui.theme.AgroTextPrimary

/**
 * High-fidelity AgroStock Branding Logo Composable matching Image 1:
 * - Lush green tree with arching leafy branches
 * - Stylized "AgroStock" typography with red apple for 'o'
 * - Resting Holstein cow with black/white patches under the tree
 */
@Composable
fun AgroStockLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 150.dp,
    compact: Boolean = false
) {
    if (compact) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(28.dp)) {
                val cWidth = this.size.width
                val cHeight = this.size.height
                val green = Color(0xFF4CAF50)
                val darkGreen = Color(0xFF2E7D32)

                drawCircle(color = green, radius = cWidth * 0.35f, center = Offset(cWidth * 0.4f, cHeight * 0.4f))
                drawCircle(color = darkGreen, radius = cWidth * 0.25f, center = Offset(cWidth * 0.6f, cHeight * 0.3f))

                val path = Path().apply {
                    moveTo(cWidth * 0.35f, cHeight * 0.85f)
                    quadraticTo(cWidth * 0.35f, cHeight * 0.5f, cWidth * 0.45f, cHeight * 0.35f)
                    lineTo(cWidth * 0.55f, cHeight * 0.4f)
                    quadraticTo(cWidth * 0.45f, cHeight * 0.6f, cWidth * 0.45f, cHeight * 0.85f)
                    close()
                }
                drawPath(path, color = darkGreen)
            }
            Spacer(modifier = Modifier.width(6.dp))
            AgroStockText(fontSize = 18.sp)
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(logoSize * 2.2f)
                    .height(logoSize),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = this.size.width
                    val h = this.size.height

                    val leafGreen = Color(0xFF558B2F)
                    val leafDarkGreen = Color(0xFF33691E)
                    val trunkColor = Color(0xFF2E7D32)
                    val grassColor = Color(0xFFE8F5E9)
                    val cowWhite = Color(0xFFFFFFFF)
                    val cowBlack = Color(0xFF1E1E1E)
                    val cowPink = Color(0xFFFFCDD2)

                    // 1. Light Grass Shadow Oval beneath cow and tree
                    drawOval(
                        color = grassColor,
                        topLeft = Offset(w * 0.30f, h * 0.80f),
                        size = Size(w * 0.58f, h * 0.14f)
                    )

                    // 2. Curving Tree Trunk on the left side
                    val trunkPath = Path().apply {
                        moveTo(w * 0.28f, h * 0.85f)
                        cubicTo(
                            w * 0.34f, h * 0.80f,
                            w * 0.38f, h * 0.65f,
                            w * 0.38f, h * 0.48f
                        )
                        cubicTo(
                            w * 0.38f, h * 0.36f,
                            w * 0.42f, h * 0.28f,
                            w * 0.45f, h * 0.22f
                        )
                        cubicTo(
                            w * 0.42f, h * 0.32f,
                            w * 0.35f, h * 0.50f,
                            w * 0.32f, h * 0.68f
                        )
                        cubicTo(
                            w * 0.30f, h * 0.78f,
                            w * 0.22f, h * 0.83f,
                            w * 0.20f, h * 0.85f
                        )
                        close()
                    }
                    drawPath(trunkPath, color = trunkColor)

                    // 3. Tree Leaves Arch over the left & top
                    val leaves = listOf(
                        Offset(w * 0.12f, h * 0.52f) to 18.dp.toPx(),
                        Offset(w * 0.10f, h * 0.40f) to 22.dp.toPx(),
                        Offset(w * 0.16f, h * 0.28f) to 26.dp.toPx(),
                        Offset(w * 0.26f, h * 0.18f) to 30.dp.toPx(),
                        Offset(w * 0.38f, h * 0.15f) to 28.dp.toPx(),
                        Offset(w * 0.48f, h * 0.18f) to 24.dp.toPx(),
                        Offset(w * 0.22f, h * 0.38f) to 32.dp.toPx(),
                        Offset(w * 0.30f, h * 0.28f) to 34.dp.toPx(),
                        Offset(w * 0.40f, h * 0.25f) to 26.dp.toPx()
                    )

                    leaves.forEachIndexed { i, (center, radius) ->
                        val color = if (i % 2 == 0) leafGreen else leafDarkGreen
                        drawCircle(color = color, radius = radius, center = center)
                    }

                    // 4. Detailed Holstein Cow sitting under tree
                    val cowX = w * 0.58f
                    val cowY = h * 0.72f

                    // Cow Body (White)
                    drawOval(
                        color = cowWhite,
                        topLeft = Offset(cowX - 32.dp.toPx(), cowY - 12.dp.toPx()),
                        size = Size(64.dp.toPx(), 26.dp.toPx())
                    )
                    drawOval(
                        color = cowBlack,
                        topLeft = Offset(cowX - 32.dp.toPx(), cowY - 12.dp.toPx()),
                        size = Size(64.dp.toPx(), 26.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // Cow Black Patches
                    drawCircle(color = cowBlack, radius = 8.dp.toPx(), center = Offset(cowX - 12.dp.toPx(), cowY - 2.dp.toPx()))
                    drawCircle(color = cowBlack, radius = 10.dp.toPx(), center = Offset(cowX + 10.dp.toPx(), cowY - 4.dp.toPx()))

                    // Cow Head & Ears
                    val headX = cowX + 22.dp.toPx()
                    val headY = cowY - 10.dp.toPx()

                    // Head base
                    drawCircle(color = cowWhite, radius = 10.dp.toPx(), center = Offset(headX, headY))
                    drawCircle(color = cowBlack, radius = 10.dp.toPx(), center = Offset(headX, headY), style = Stroke(width = 1.2.dp.toPx()))
                    // Head patch
                    drawCircle(color = cowBlack, radius = 4.dp.toPx(), center = Offset(headX - 2.dp.toPx(), headY - 4.dp.toPx()))
                    // Pink Snout
                    drawOval(
                        color = cowPink,
                        topLeft = Offset(headX + 4.dp.toPx(), headY - 2.dp.toPx()),
                        size = Size(9.dp.toPx(), 7.dp.toPx())
                    )
                    drawOval(
                        color = cowBlack,
                        topLeft = Offset(headX + 4.dp.toPx(), headY - 2.dp.toPx()),
                        size = Size(9.dp.toPx(), 7.dp.toPx()),
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }

                // 5. High-Contrast Typography Row on the Right Side
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AgroStockText(fontSize = 32.sp)
                }
            }
        }
    }
}

@Composable
fun AgroStockText(
    fontSize: androidx.compose.ui.unit.TextUnit = 28.sp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AgroGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                ) {
                    append("Agro")
                }
                withStyle(
                    style = SpanStyle(
                        color = AgroTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )
                ) {
                    append("St")
                }
            },
            fontSize = fontSize
        )
        // Red Apple Icon
        Box(
            modifier = Modifier.padding(horizontal = 1.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.size(fontSize.value.dp * 0.75f)) {
                val cWidth = this.size.width
                val cHeight = this.size.height

                // Red Apple
                drawCircle(
                    color = AgroRedControlled,
                    radius = cWidth * 0.42f,
                    center = Offset(cWidth * 0.5f, cHeight * 0.58f)
                )
                // Green Leaf
                val leafPath = Path().apply {
                    moveTo(cWidth * 0.5f, cHeight * 0.2f)
                    quadraticTo(cWidth * 0.7f, cHeight * 0.1f, cWidth * 0.8f, cHeight * 0.3f)
                    quadraticTo(cWidth * 0.6f, cHeight * 0.35f, cWidth * 0.5f, cHeight * 0.2f)
                    close()
                }
                drawPath(leafPath, color = Color(0xFF4CAF50))
            }
        }
        Text(
            text = "ck",
            color = AgroTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSize
        )
    }
}
