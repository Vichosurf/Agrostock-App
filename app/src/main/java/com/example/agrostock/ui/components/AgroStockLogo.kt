package com.example.agrostock.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
 * AgroStock Branding Logo Composable matching Image 1:
 * - Lush green tree with leaves
 * - Stylized "AgroStock" text with red apple for 'o'
 * - Resting Holstein cow under the tree
 */
@Composable
fun AgroStockLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 140.dp,
    compact: Boolean = false
) {
    if (compact) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small Tree Graphic
            Canvas(modifier = Modifier.size(28.dp)) {
                val cWidth = this.size.width
                val cHeight = this.size.height
                val green = Color(0xFF4CAF50)
                val darkGreen = Color(0xFF2E7D32)
                
                // Leaves cluster
                drawCircle(color = green, radius = cWidth * 0.35f, center = Offset(cWidth * 0.4f, cHeight * 0.4f))
                drawCircle(color = darkGreen, radius = cWidth * 0.25f, center = Offset(cWidth * 0.6f, cHeight * 0.3f))
                // Trunk
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
                    .width(logoSize * 1.8f)
                    .height(logoSize),
                contentAlignment = Alignment.Center
            ) {
                // Drawing Tree, Cow, and AgroStock Text according to Image 1
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = this.size.width
                    val height = this.size.height

                    val greenTree = Color(0xFF4C9A2A)
                    val darkGreenTree = Color(0xFF2D6A1F)
                    val trunkColor = Color(0xFF388E3C)
                    val groundColor = Color(0xFFDCEDC8)
                    val cowWhite = Color(0xFFFAFAFA)
                    val cowBlack = Color(0xFF212121)
                    val cowPink = Color(0xFFFFCDD2)

                    // Ground shadow
                    drawOval(
                        color = groundColor,
                        topLeft = Offset(width * 0.32f, height * 0.82f),
                        size = Size(width * 0.55f, height * 0.12f)
                    )

                    // Tree Trunk curved into 'A'
                    val trunkPath = Path().apply {
                        moveTo(width * 0.3f, height * 0.82f)
                        cubicTo(
                            width * 0.38f, height * 0.78f,
                            width * 0.42f, height * 0.62f,
                            width * 0.42f, height * 0.45f
                        )
                        cubicTo(
                            width * 0.42f, height * 0.35f,
                            width * 0.45f, height * 0.28f,
                            width * 0.48f, height * 0.22f
                        )
                        cubicTo(
                            width * 0.45f, height * 0.32f,
                            width * 0.39f, height * 0.48f,
                            width * 0.36f, height * 0.65f
                        )
                        cubicTo(
                            width * 0.34f, height * 0.75f,
                            width * 0.26f, height * 0.82f,
                            width * 0.24f, height * 0.83f
                        )
                        close()
                    }
                    drawPath(trunkPath, color = trunkColor)

                    // Tree Leaves Branches (Arc of green foliage)
                    val leafCenters = listOf(
                        Offset(width * 0.18f, height * 0.50f) to 22.dp.toPx(),
                        Offset(width * 0.14f, height * 0.38f) to 26.dp.toPx(),
                        Offset(width * 0.22f, height * 0.28f) to 30.dp.toPx(),
                        Offset(width * 0.32f, height * 0.20f) to 34.dp.toPx(),
                        Offset(width * 0.44f, height * 0.18f) to 32.dp.toPx(),
                        Offset(width * 0.54f, height * 0.24f) to 28.dp.toPx(),
                        Offset(width * 0.62f, height * 0.34f) to 22.dp.toPx(),
                        Offset(width * 0.28f, height * 0.38f) to 36.dp.toPx(),
                        Offset(width * 0.38f, height * 0.30f) to 38.dp.toPx(),
                        Offset(width * 0.48f, height * 0.32f) to 30.dp.toPx()
                    )

                    leafCenters.forEachIndexed { idx, (center, r) ->
                        val c = if (idx % 2 == 0) greenTree else darkGreenTree
                        drawCircle(color = c, radius = r, center = center)
                    }

                    // Cow resting under tree
                    val cowCenterX = width * 0.60f
                    val cowCenterY = height * 0.72f

                    // Cow Body (White oval)
                    drawOval(
                        color = cowWhite,
                        topLeft = Offset(cowCenterX - 34.dp.toPx(), cowCenterY - 14.dp.toPx()),
                        size = Size(68.dp.toPx(), 28.dp.toPx())
                    )
                    drawOval(
                        color = cowBlack,
                        topLeft = Offset(cowCenterX - 34.dp.toPx(), cowCenterY - 14.dp.toPx()),
                        size = Size(68.dp.toPx(), 28.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Cow Black Spots
                    drawCircle(color = cowBlack, radius = 9.dp.toPx(), center = Offset(cowCenterX - 10.dp.toPx(), cowCenterY - 2.dp.toPx()))
                    drawCircle(color = cowBlack, radius = 12.dp.toPx(), center = Offset(cowCenterX + 12.dp.toPx(), cowCenterY - 4.dp.toPx()))

                    // Cow Head
                    val headX = cowCenterX + 22.dp.toPx()
                    val headY = cowCenterY - 10.dp.toPx()
                    drawCircle(color = cowWhite, radius = 11.dp.toPx(), center = Offset(headX, headY))
                    drawCircle(color = cowBlack, radius = 11.dp.toPx(), center = Offset(headX, headY), style = Stroke(width = 1.5.dp.toPx()))
                    // Head spot
                    drawCircle(color = cowBlack, radius = 5.dp.toPx(), center = Offset(headX - 3.dp.toPx(), headY - 4.dp.toPx()))
                    // Snout pink
                    drawCircle(color = cowPink, radius = 5.dp.toPx(), center = Offset(headX + 8.dp.toPx(), headY + 2.dp.toPx()))
                }

                // Text Overlay: AgroStock
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp, bottom = 28.dp)
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
        // Apple for 'o'
        Box(
            modifier = Modifier.padding(horizontal = 1.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.size(fontSize.value.dp * 0.75f)) {
                val cWidth = this.size.width
                val cHeight = this.size.height

                // Red Apple Body
                drawCircle(
                    color = AgroRedControlled,
                    radius = cWidth * 0.42f,
                    center = Offset(cWidth * 0.5f, cHeight * 0.58f)
                )
                // Small Green Leaf on Apple
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
