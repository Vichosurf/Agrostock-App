package com.example.agrostock.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** PBKDF2-HMAC-SHA256 PIN verifier; the raw PIN is never persisted. */
object PinHasher {
    private const val PREFIX = "pbkdf2-sha256"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256

    fun hash(pin: String): String {
        require(pin.length == 6 && pin.all { it.isDigit() }) {
            "El PIN debe contener exactamente seis dígitos."
        }
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val derived = derive(pin, salt, ITERATIONS)
        val separator = "\u0024"
        return listOf(
            PREFIX,
            ITERATIONS.toString(),
            Base64.encodeToString(salt, Base64.NO_WRAP),
            Base64.encodeToString(derived, Base64.NO_WRAP)
        ).joinToString(separator)
    }

    fun verify(pin: String, encoded: String): Boolean {
        val parts = encoded.split("\u0024")
        if (parts.size != 4 || parts[0] != PREFIX) return false
        val iterations = parts[1].toIntOrNull() ?: return false
        if (iterations < 100_000 || pin.length != 6 || !pin.all { it.isDigit() }) return false
        return try {
            val salt = Base64.decode(parts[2], Base64.NO_WRAP)
            val expected = Base64.decode(parts[3], Base64.NO_WRAP)
            MessageDigest.isEqual(expected, derive(pin, salt, iterations))
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    fun isEncoded(value: String): Boolean = value.startsWith(PREFIX + "\u0024")

    private fun derive(pin: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
