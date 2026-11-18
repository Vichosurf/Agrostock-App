package com.example.agrostock

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.agrostock.data.UserAccount
import com.example.agrostock.data.local.AgroStockSQLiteHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var context: Context
    private lateinit var db: AgroStockSQLiteHelper

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("agrostock_database.db")
        db = AgroStockSQLiteHelper(context)
    }

    @After
    fun tearDown() {
        db.close()
        context.deleteDatabase("agrostock_database.db")
    }

    @Test
    fun appContextAndDatabaseSchemaAreAvailable() {
        assertEquals("com.example.agrostock", context.packageName)
        val tables = db.readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'android_%'",
            null
        ).use { cursor ->
            val names = mutableSetOf<String>()
            while (cursor.moveToNext()) names.add(cursor.getString(0))
            names
        }
        assertTrue(tables.containsAll(setOf(
            "rol", "usuario", "bodega", "categoria", "producto",
            "animal", "proveedor", "movimiento", "registro_auditoria", "sync_outbox"
        )))
    }

    @Test
    fun registrationStoresVerifierInsteadOfRawPin() {
        assertTrue(db.insertUsuario(UserAccount("12.345.678-5", "123456")))
        assertTrue(db.authenticateUsuario("12.345.678-5", "123456"))
        assertFalse(db.authenticateUsuario("12.345.678-5", "000000"))

        val stored = db.readableDatabase.rawQuery(
            "SELECT pin_hash FROM usuario WHERE identificador = ?",
            arrayOf("12.345.678-5")
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            cursor.getString(0)
        }
        assertNotEquals("123456", stored)
        assertTrue(stored.startsWith("pbkdf2-sha256"))
    }

    @Test
    fun withdrawalPersistsExactQuantityUpdatesStockAndCreatesOutboxAtomically() {
        assertTrue(db.insertUsuario(UserAccount("12.345.678-5", "123456")))
        val product = db.fetchProducts().first { it.category == "Forrajes" }
        val before = product.stock

        db.insertWithdrawal("12.345.678-5", product.id, 3, null)

        val after = db.fetchProducts().first { it.id == product.id }.stock
        assertEquals(before - 3.0, after, 0.001)
        assertEquals(1, db.getPendingSyncEvents().size)
        assertEquals(3.0, db.getAllMovimientos().first().title.substringBefore(" ").toDouble(), 0.001)
    }

    @Test
    fun withdrawalAboveStockDoesNotChangeStockOrCreateOutbox() {
        assertTrue(db.insertUsuario(UserAccount("12.345.678-5", "123456")))
        val product = db.fetchProducts().first { it.category == "Forrajes" }
        val before = product.stock

        assertThrows(IllegalArgumentException::class.java) {
            db.insertWithdrawal("12.345.678-5", product.id, before.toInt() + 1, null)
        }

        assertEquals(before, db.fetchProducts().first { it.id == product.id }.stock, 0.001)
        assertTrue(db.getPendingSyncEvents().isEmpty())
    }

    @Test
    fun traceableProductRequiresAnActiveRegisteredAnimal() {
        assertTrue(db.insertUsuario(UserAccount("12.345.678-5", "123456")))
        val product = db.fetchProducts().first { it.requiresDiio }

        assertThrows(IllegalArgumentException::class.java) {
            db.insertWithdrawal("12.345.678-5", product.id, 1, "999999999")
        }
        db.insertWithdrawal("12.345.678-5", product.id, 1, "123456789")
        assertEquals(1, db.getPendingSyncEvents().size)
    }

    @Test
    fun foreignKeyEnforcementIsEnabled() {
        val enabled = db.readableDatabase.rawQuery("PRAGMA foreign_keys", null).use {
            it.moveToFirst() && it.getInt(0) == 1
        }
        assertTrue(enabled)
    }
}
