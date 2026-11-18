package com.example.agrostock.data

data class InventoryProduct(
    val id: Long,
    val name: String,
    val category: String,
    val stock: Double,
    val unit: String,
    val requiresDiio: Boolean
)

data class PendingSyncEvent(
    val eventId: String,
    val transactionId: String,
    val json: String
)
