package com.example.agrostock

import com.example.agrostock.util.RutUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun rutValidatesKnownValidRut() {
        assertTrue(RutUtils.isValidRut("12.345.678-5"))
    }

    @Test
    fun rutRejectsIncorrectCheckDigit() {
        assertFalse(RutUtils.isValidRut("12.345.678-9"))
    }

    @Test
    fun rutFormatsInputConsistently() {
        assertEquals("12.345.678-5", RutUtils.formatRut("123456785"))
        assertEquals("5.108.566-3", RutUtils.formatRut("51085663"))
    }
}
