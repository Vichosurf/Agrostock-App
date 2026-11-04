package com.example.agrostock.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agrostock.R

/**
 * High-definition AgroStock Branding Logo Composable.
 * Renders the original high-resolution logo image (Image 1) from res/drawable/agrostock_logo.png
 * for 100% crisp, un-blurred, pixel-perfect visual fidelity.
 */
@Composable
fun AgroStockLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 140.dp,
    compact: Boolean = false
) {
    val displaySize = if (compact) 32.dp else logoSize

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.agrostock_logo),
            contentDescription = "AgroStock Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(displaySize)
        )
    }
}
