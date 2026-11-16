package com.example.agrostock.data

/**
 * User Account Entity Model for AgroStock Authentication (RUT and 6-digit PIN).
 */
data class UserAccount(
    val rut: String,
    val pin: String
)
