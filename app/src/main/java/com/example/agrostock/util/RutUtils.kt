package com.example.agrostock.util

/**
 * Chilean RUT Utilities:
 * - Character Filtering (only digits and 'K'/'k')
 * - Automatic Formatting (XX.XXX.XXX-Y or X.XXX.XXX-Y)
 * - Modulo 11 Verifier Algorithm Calculation & Validation
 */
object RutUtils {

    /**
     * Cleans input to contain only numeric digits and uppercase 'K'.
     */
    fun cleanRut(input: String): String {
        return input.filter { it.isDigit() || it == 'k' || it == 'K' }.uppercase()
    }

    /**
     * Calculates the expected Modulo 11 verifier digit (DV) for a given RUT body.
     */
    fun calculateDv(bodyDigits: String): String {
        val cleanBody = bodyDigits.filter { it.isDigit() }
        if (cleanBody.isEmpty()) return ""

        var sum = 0
        var multiplier = 2

        for (i in cleanBody.length - 1 downTo 0) {
            val digit = cleanBody[i].digitToInt()
            sum += digit * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }

        val remainder = 11 - (sum % 11)
        return when (remainder) {
            11 -> "0"
            10 -> "K"
            else -> remainder.toString()
        }
    }

    /**
     * Validates if a Chilean RUT has a valid body length (7-8 digits) and matching Modulo 11 DV.
     */
    fun isValidRut(input: String): Boolean {
        val clean = cleanRut(input)
        if (clean.length !in 8..9) return false

        val body = clean.dropLast(1)
        val enteredDv = clean.takeLast(1)
        val expectedDv = calculateDv(body)

        return enteredDv.equals(expectedDv, ignoreCase = true)
    }

    /**
     * Formats clean or unformatted RUT string into standard Chilean format: XX.XXX.XXX-Y
     * Examples:
     * - "123456785" -> "12.345.678-5"
     * - "51085663"  -> "5.108.566-3"
     * - "25691171K" -> "25.691.171-K"
     */
    fun formatRut(input: String): String {
        val clean = cleanRut(input)
        if (clean.length < 2) return clean

        val dv = clean.takeLast(1)
        val body = clean.dropLast(1)

        val formattedBody = StringBuilder()
        var count = 0

        for (i in body.length - 1 downTo 0) {
            formattedBody.append(body[i])
            count++
            if (count % 3 == 0 && i > 0) {
                formattedBody.append('.')
            }
        }

        return "${formattedBody.reverse()}-$dv"
    }
}
