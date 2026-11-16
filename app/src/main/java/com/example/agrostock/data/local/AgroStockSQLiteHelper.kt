package com.example.agrostock.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction

/**
 * Native Android SQLite Database Helper for AgroStock App.
 * Manages local persistent storage for users, catalog products, and offline withdrawals.
 */
class AgroStockSQLiteHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "agrostock_database.db"
        private const val DATABASE_VERSION = 1

        // Table Users
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_RUT = "rut"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PIN = "pin"

        // Table Products
        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_PRODUCT_ID = "id"
        private const val COLUMN_PRODUCT_NAME = "name"
        private const val COLUMN_PRODUCT_CATEGORY = "category"
        private const val COLUMN_PRODUCT_STOCK = "stock"

        // Table Withdrawals
        private const val TABLE_WITHDRAWALS = "withdrawals"
        private const val COLUMN_WITHDRAWAL_ID = "id"
        private const val COLUMN_WITHDRAWAL_TITLE = "title"
        private const val COLUMN_WITHDRAWAL_DIIO = "diio"
        private const val COLUMN_WITHDRAWAL_TIME_LOCATION = "time_location"
        private const val COLUMN_WITHDRAWAL_IS_SYNCED = "is_synced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_RUT TEXT PRIMARY KEY,
                $COLUMN_USER_EMAIL TEXT NOT NULL,
                $COLUMN_USER_PIN TEXT NOT NULL
            )
        """.trimIndent()

        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID TEXT PRIMARY KEY,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_STOCK TEXT NOT NULL
            )
        """.trimIndent()

        val createWithdrawalsTable = """
            CREATE TABLE $TABLE_WITHDRAWALS (
                $COLUMN_WITHDRAWAL_ID TEXT PRIMARY KEY,
                $COLUMN_WITHDRAWAL_TITLE TEXT NOT NULL,
                $COLUMN_WITHDRAWAL_DIIO TEXT,
                $COLUMN_WITHDRAWAL_TIME_LOCATION TEXT NOT NULL,
                $COLUMN_WITHDRAWAL_IS_SYNCED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createProductsTable)
        db.execSQL(createWithdrawalsTable)

        // Seed initial demo data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WITHDRAWALS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Seed initial users
        val users = listOf(
            ContentValues().apply {
                put(COLUMN_USER_RUT, "12.345.678-5")
                put(COLUMN_USER_EMAIL, "operador1@agrostock.cl")
                put(COLUMN_USER_PIN, "123456")
            },
            ContentValues().apply {
                put(COLUMN_USER_RUT, "5.108.566-3")
                put(COLUMN_USER_EMAIL, "operador2@agrostock.cl")
                put(COLUMN_USER_PIN, "654321")
            },
            ContentValues().apply {
                put(COLUMN_USER_RUT, "25.691.171-K")
                put(COLUMN_USER_EMAIL, "operador3@agrostock.cl")
                put(COLUMN_USER_PIN, "112233")
            }
        )
        users.forEach { db.insert(TABLE_USERS, null, it) }

        // Seed initial withdrawals
        val withdrawals = listOf(
            ContentValues().apply {
                put(COLUMN_WITHDRAWAL_ID, "TX-2024-0081")
                put(COLUMN_WITHDRAWAL_TITLE, "15 sacos de Concentrado")
                put(COLUMN_WITHDRAWAL_DIIO, null as String?)
                put(COLUMN_WITHDRAWAL_TIME_LOCATION, "Hace 10 min · Galpón 2")
                put(COLUMN_WITHDRAWAL_IS_SYNCED, 0)
            },
            ContentValues().apply {
                put(COLUMN_WITHDRAWAL_ID, "TX-2024-0080")
                put(COLUMN_WITHDRAWAL_TITLE, "2 fármacos (Penicilina Bov 100ml)")
                put(COLUMN_WITHDRAWAL_DIIO, "DIIO: 123.456.789")
                put(COLUMN_WITHDRAWAL_TIME_LOCATION, "Hace 25 min · Enfermería")
                put(COLUMN_WITHDRAWAL_IS_SYNCED, 0)
            },
            ContentValues().apply {
                put(COLUMN_WITHDRAWAL_ID, "TX-2024-0079")
                put(COLUMN_WITHDRAWAL_TITLE, "50 kg Sal Mineral Bloque")
                put(COLUMN_WITHDRAWAL_DIIO, null as String?)
                put(COLUMN_WITHDRAWAL_TIME_LOCATION, "Hace 40 min · Potrero 4")
                put(COLUMN_WITHDRAWAL_IS_SYNCED, 0)
            },
            ContentValues().apply {
                put(COLUMN_WITHDRAWAL_ID, "TX-2024-0078")
                put(COLUMN_WITHDRAWAL_TITLE, "10 sacos Fertilizante Urea")
                put(COLUMN_WITHDRAWAL_DIIO, null as String?)
                put(COLUMN_WITHDRAWAL_TIME_LOCATION, "Hace 1 hora · Bodega Central")
                put(COLUMN_WITHDRAWAL_IS_SYNCED, 0)
            },
            ContentValues().apply {
                put(COLUMN_WITHDRAWAL_ID, "TX-2024-0077")
                put(COLUMN_WITHDRAWAL_TITLE, "1 frasco Ivermectina 500ml")
                put(COLUMN_WITHDRAWAL_DIIO, null as String?)
                put(COLUMN_WITHDRAWAL_TIME_LOCATION, "Hace 2 horas · Bodega Vivero")
                put(COLUMN_WITHDRAWAL_IS_SYNCED, 0)
            }
        )
        withdrawals.forEach { db.insert(TABLE_WITHDRAWALS, null, it) }
    }

    // --- USER DATABASE OPERATIONS ---

    fun insertUser(user: UserAccount): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_USER_RUT, user.rut)
            put(COLUMN_USER_EMAIL, user.email)
            put(COLUMN_USER_PIN, user.pin)
        }
        val result = db.insert(TABLE_USERS, null, cv)
        return result != -1L
    }

    fun authenticateUser(rut: String, pin: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_RUT),
            "$COLUMN_USER_RUT = ? AND $COLUMN_USER_PIN = ?",
            arrayOf(rut, pin),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun userExists(rut: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_RUT),
            "$COLUMN_USER_RUT = ?",
            arrayOf(rut),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // --- WITHDRAWALS DATABASE OPERATIONS ---

    fun insertWithdrawal(tx: OfflineTransaction): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_WITHDRAWAL_ID, tx.id)
            put(COLUMN_WITHDRAWAL_TITLE, tx.title)
            put(COLUMN_WITHDRAWAL_DIIO, tx.diio)
            put(COLUMN_WITHDRAWAL_TIME_LOCATION, tx.timeLocation)
            put(COLUMN_WITHDRAWAL_IS_SYNCED, if (tx.isSynced) 1 else 0)
        }
        val result = db.insertWithOnConflict(
            TABLE_WITHDRAWALS,
            null,
            cv,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getAllWithdrawals(): List<OfflineTransaction> {
        val list = mutableListOf<OfflineTransaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WITHDRAWALS ORDER BY rowid DESC", null)

        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow(COLUMN_WITHDRAWAL_ID)
            val titleIdx = cursor.getColumnIndexOrThrow(COLUMN_WITHDRAWAL_TITLE)
            val diioIdx = cursor.getColumnIndexOrThrow(COLUMN_WITHDRAWAL_DIIO)
            val timeLocIdx = cursor.getColumnIndexOrThrow(COLUMN_WITHDRAWAL_TIME_LOCATION)
            val syncedIdx = cursor.getColumnIndexOrThrow(COLUMN_WITHDRAWAL_IS_SYNCED)

            do {
                list.add(
                    OfflineTransaction(
                        id = cursor.getString(idIdx),
                        title = cursor.getString(titleIdx),
                        timeLocation = cursor.getString(timeLocIdx),
                        diio = if (cursor.isNull(diioIdx)) null else cursor.getString(diioIdx),
                        isSynced = cursor.getInt(syncedIdx) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun markAllWithdrawalsSynced() {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_WITHDRAWAL_IS_SYNCED, 1)
        }
        db.update(TABLE_WITHDRAWALS, cv, null, null)
    }
}
