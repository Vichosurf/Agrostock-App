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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrostock.ui.components.AgroStockLogo
import com.example.agrostock.ui.theme.AgroBackground
import com.example.agrostock.ui.theme.AgroBorder
import com.example.agrostock.ui.theme.AgroGreenDark
import com.example.agrostock.ui.theme.AgroGreenPrimary
import com.example.agrostock.ui.theme.AgroOfflineOrange
import com.example.agrostock.ui.theme.AgroTextPrimary
import com.example.agrostock.ui.theme.AgroTextSecondary

data class ProductItem(
    val id: String,
    val name: String,
    val category: String,
    val stock: String,
    val icon: ImageVector,
    val buttonText: String,
    val buttonColor: Color
)

/**
 * Screen 3: Catálogo y Búsqueda (Online/Offline)
 * Matching Mockup 3 from Image 2
 */
@Composable
fun CatalogoScreen(
    userRut: String = "14.821.903-K",
    isOnline: Boolean = true,
    onSelectProduct: (ProductItem) -> Unit = {},
    onGoToWithdrawal: () -> Unit = {},
    onGoToMovements: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("TODOS") }

    val allProducts = listOf(
        ProductItem(
            id = "1",
            name = "Saco de Concentrado",
            category = "Forrajes",
            stock = "Stock: 124 sacos",
            icon = Icons.Default.Grass,
            buttonText = "FORRAJE >",
            buttonColor = AgroGreenPrimary
        ),
        ProductItem(
            id = "2",
            name = "Sal Mineral",
            category = "Sales",
            stock = "Stock: 48 bloques",
            icon = Icons.Default.Star,
            buttonText = "SALES >",
            buttonColor = AgroGreenPrimary
        ),
        ProductItem(
            id = "3",
            name = "Penicilina Retard",
            category = "Fármacos",
            stock = "Stock: 24 frascos",
            icon = Icons.Default.LocalPharmacy,
            buttonText = "FÁRMACOS >",
            buttonColor = AgroGreenPrimary
        ),
        ProductItem(
            id = "4",
            name = "Fertilizante Urea",
            category = "Fertilizantes",
            stock = "Stock: 90 sacos",
            icon = Icons.Default.Science,
            buttonText = "FERTILIZANTES >",
            buttonColor = AgroGreenPrimary
        )
    )

    val filteredProducts = allProducts.filter { product ->
        val matchesCategory = selectedCategory == "TODOS" || product.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroBackground)
    ) {
        // Mode Header Bar
        val modeColor = if (isOnline) AgroGreenDark else Color(0xFF374151)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = modeColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = "Network",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "● Modo Online (Con Wi-Fi) - Sincronizado" else "● Modo Offline - Guardado Local",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    color = if (isOnline) Color(0xFF4CAF50) else AgroOfflineOrange,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isOnline) "Active" else "Local",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Main Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OPERACIÓN EN TERRENO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgroTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "SELECCIONAR INSUMO",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = AgroTextPrimary
                    )
                }
                AgroStockLogo(compact = true)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text("Buscador de insumos...", fontSize = 13.sp, color = AgroTextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AgroTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AgroGreenPrimary,
                    unfocusedBorderColor = AgroBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CATEGORÍAS Section
            Text(
                text = "CATEGORÍAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AgroTextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryIconButton(
                    label = "Forrajes",
                    icon = Icons.Default.Grass,
                    bgColor = Color(0xFFE8F5E9),
                    iconColor = AgroGreenPrimary,
                    isSelected = selectedCategory == "Forrajes",
                    onClick = {
                        selectedCategory = if (selectedCategory == "Forrajes") "TODOS" else "Forrajes"
                    }
                )
                CategoryIconButton(
                    label = "Sales",
                    icon = Icons.Default.Star,
                    bgColor = Color(0xFFFFF3E0),
                    iconColor = Color(0xFFE65100),
                    isSelected = selectedCategory == "Sales",
                    onClick = {
                        selectedCategory = if (selectedCategory == "Sales") "TODOS" else "Sales"
                    }
                )
                CategoryIconButton(
                    label = "Fármacos",
                    icon = Icons.Default.LocalPharmacy,
                    bgColor = Color(0xFFFFEBEE),
                    iconColor = Color(0xFFC62828),
                    isSelected = selectedCategory == "Fármacos",
                    onClick = {
                        selectedCategory = if (selectedCategory == "Fármacos") "TODOS" else "Fármacos"
                    }
                )
                CategoryIconButton(
                    label = "Fertilizantes",
                    icon = Icons.Default.Science,
                    bgColor = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF1565C0),
                    isSelected = selectedCategory == "Fertilizantes",
                    onClick = {
                        selectedCategory = if (selectedCategory == "Fertilizantes") "TODOS" else "Fertilizantes"
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PRODUCTOS Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRODUCTOS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgroTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${filteredProducts.size} disponibles",
                    fontSize = 11.sp,
                    color = AgroTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Cards Grid (2 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts) { product ->
                    ProductCardItem(
                        product = product,
                        onClick = { onSelectProduct(product) }
                    )
                }
            }
        }

        // Bottom User Context Bar & Action Buttons
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // User RUT Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤 RUT: $userRut  Turno Mañana · Módulo Fundo",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgroTextPrimary
                        )
                        Surface(
                            color = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isOnline) "En Línea" else "Offline",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) AgroGreenPrimary else AgroOfflineOrange,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoToMovements,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgroTextSecondary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = "Movements",
                                tint = AgroTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MOVIMIENTOS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextPrimary
                            )
                        }
                    }

                    Button(
                        onClick = onGoToWithdrawal,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgroGreenDark)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+ NUEVO RETIRO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryIconButton(
    label: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.5.dp else 0.dp,
                    color = if (isSelected) AgroGreenPrimary else Color.Transparent,
                    shape = CircleShape
                ),
            color = bgColor,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = AgroTextPrimary
        )
    }
}

@Composable
private fun ProductCardItem(
    product: ProductItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgroBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Placeholder Box
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Color(0xFFF8F9FA)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = product.icon,
                        contentDescription = product.name,
                        tint = AgroGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AgroTextPrimary,
                maxLines = 1
            )

            Text(
                text = product.stock,
                fontSize = 10.sp,
                color = AgroTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button inside Card
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = product.buttonColor)
            ) {
                Text(
                    text = product.buttonText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}
