package com.example.agrostock.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction
import java.util.UUID

/**
 * Native Android SQLite Database Helper for AgroStock App matching the official schema from Informe AgroStock.
 * Contains the 6 relational tables: usuario, bodega, categoria, producto, animal, and movimiento.
 */
class AgroStockSQLiteHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "agrostock_database.db"
        private const val DATABASE_VERSION = 3

        // Table usuario
        private const val TABLE_USUARIO = "usuario"
        private const val COL_USUARIO_ID = "id_usuario"
        private const val COL_USUARIO_RUT = "rut"
        private const val COL_USUARIO_EMAIL = "email"
        private const val COL_USUARIO_PIN = "pin"
        private const val COL_USUARIO_ACTIVO = "activo"

        // Table bodega
        private const val TABLE_BODEGA = "bodega"
        private const val COL_BODEGA_ID = "id_bodega"
        private const val COL_BODEGA_NOMBRE = "nombre_ubicacion"
        private const val COL_BODEGA_SECTOR = "sector_predio"

        // Table categoria
        private const val TABLE_CATEGORIA = "categoria"
        private const val COL_CATEGORIA_ID = "id_categoria"
        private const val COL_CATEGORIA_NOMBRE = "nombre_categoria"
        private const val COL_CATEGORIA_EXIGE_TRAZABILIDAD = "exige_trazabilidad"

        // Table producto
        private const val TABLE_PRODUCTO = "producto"
        private const val COL_PRODUCTO_ID = "id_producto"
        private const val COL_PRODUCTO_SKU = "codigo_sku"
        private const val COL_PRODUCTO_DESCRIPCION = "descripcion"
        private const val COL_PRODUCTO_UNIDAD = "unidad_medida"
        private const val COL_PRODUCTO_STOCK = "stock_actual"
        private const val COL_PRODUCTO_ID_CATEGORIA = "id_categoria"
        private const val COL_PRODUCTO_ID_BODEGA = "id_bodega"

        // Table animal
        private const val TABLE_ANIMAL = "animal"
        private const val COL_ANIMAL_ID = "id_animal"
        private const val COL_ANIMAL_DIIO = "diio"
        private const val COL_ANIMAL_ESTADO = "estado"

        // Table movimiento
        private const val TABLE_MOVIMIENTO = "movimiento"
        private const val COL_MOVIMIENTO_ID = "id_movimiento"
        private const val COL_MOVIMIENTO_TX_LOCAL = "id_transaccion_local"
        private const val COL_MOVIMIENTO_ID_USUARIO = "id_usuario"
        private const val COL_MOVIMIENTO_ID_PRODUCTO = "id_producto"
        private const val COL_MOVIMIENTO_DIIO = "diio"
        private const val COL_MOVIMIENTO_CANTIDAD = "cantidad"
        private const val COL_MOVIMIENTO_REQUIERE_DIIO = "requiere_diio"
        private const val COL_MOVIMIENTO_ESTADO_SYNC = "estado_sincronizacion"
        private const val COL_MOVIMIENTO_TIME_LOCATION = "time_location"
        private const val COL_MOVIMIENTO_CREADO_EN = "creado_en"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                $COL_USUARIO_ID TEXT PRIMARY KEY,
                $COL_USUARIO_RUT TEXT UNIQUE NOT NULL,
                $COL_USUARIO_EMAIL TEXT,
                $COL_USUARIO_PIN TEXT NOT NULL,
                $COL_USUARIO_ACTIVO INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createBodega = """
            CREATE TABLE $TABLE_BODEGA (
                $COL_BODEGA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BODEGA_NOMBRE TEXT NOT NULL,
                $COL_BODEGA_SECTOR TEXT
            )
        """.trimIndent()

        val createCategoria = """
            CREATE TABLE $TABLE_CATEGORIA (
                $COL_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORIA_NOMBRE TEXT UNIQUE NOT NULL,
                $COL_CATEGORIA_EXIGE_TRAZABILIDAD INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        val createProducto = """
            CREATE TABLE $TABLE_PRODUCTO (
                $COL_PRODUCTO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCTO_SKU TEXT UNIQUE NOT NULL,
                $COL_PRODUCTO_DESCRIPCION TEXT NOT NULL,
                $COL_PRODUCTO_UNIDAD TEXT NOT NULL,
                $COL_PRODUCTO_STOCK INTEGER NOT NULL DEFAULT 0,
                $COL_PRODUCTO_ID_CATEGORIA INTEGER REFERENCES $TABLE_CATEGORIA($COL_CATEGORIA_ID),
                $COL_PRODUCTO_ID_BODEGA INTEGER REFERENCES $TABLE_BODEGA($COL_BODEGA_ID)
            )
        """.trimIndent()

        val createAnimal = """
            CREATE TABLE $TABLE_ANIMAL (
                $COL_ANIMAL_ID TEXT PRIMARY KEY,
                $COL_ANIMAL_DIIO TEXT UNIQUE NOT NULL,
                $COL_ANIMAL_ESTADO TEXT NOT NULL DEFAULT 'Lactancia Activa'
            )
        """.trimIndent()

        val createMovimiento = """
            CREATE TABLE $TABLE_MOVIMIENTO (
                $COL_MOVIMIENTO_ID TEXT PRIMARY KEY,
                $COL_MOVIMIENTO_TX_LOCAL TEXT UNIQUE NOT NULL,
                $COL_MOVIMIENTO_ID_USUARIO TEXT,
                $COL_MOVIMIENTO_ID_PRODUCTO INTEGER,
                $COL_MOVIMIENTO_DIIO TEXT,
                $COL_MOVIMIENTO_CANTIDAD INTEGER NOT NULL,
                $COL_MOVIMIENTO_REQUIERE_DIIO INTEGER NOT NULL DEFAULT 0,
                $COL_MOVIMIENTO_ESTADO_SYNC TEXT NOT NULL DEFAULT 'SYNC_PENDING',
                $COL_MOVIMIENTO_TIME_LOCATION TEXT NOT NULL,
                $COL_MOVIMIENTO_CREADO_EN TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsuario)
        db.execSQL(createBodega)
        db.execSQL(createCategoria)
        db.execSQL(createProducto)
        db.execSQL(createAnimal)
        db.execSQL(createMovimiento)

        // Seed initial data matching official report Annex C.1
        seedOfficialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIMIENTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BODEGA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }

    private fun seedOfficialData(db: SQLiteDatabase) {
        // Seed usuarios
        val users = listOf(
            ContentValues().apply {
                put(COL_USUARIO_ID, UUID.randomUUID().toString())
                put(COL_USUARIO_RUT, "12.345.678-5")
                put(COL_USUARIO_EMAIL, "operador1@agrostock.cl")
                put(COL_USUARIO_PIN, "123456")
                put(COL_USUARIO_ACTIVO, 1)
            },
            ContentValues().apply {
                put(COL_USUARIO_ID, UUID.randomUUID().toString())
                put(COL_USUARIO_RUT, "5.108.566-3")
                put(COL_USUARIO_EMAIL, "operador2@agrostock.cl")
                put(COL_USUARIO_PIN, "654321")
                put(COL_USUARIO_ACTIVO, 1)
            },
            ContentValues().apply {
                put(COL_USUARIO_ID, UUID.randomUUID().toString())
                put(COL_USUARIO_RUT, "25.691.171-K")
                put(COL_USUARIO_EMAIL, "operador3@agrostock.cl")
                put(COL_USUARIO_PIN, "112233")
                put(COL_USUARIO_ACTIVO, 1)
            }
        )
        users.forEach { db.insert(TABLE_USUARIO, null, it) }

        // Seed bodegas
        val bodegas = listOf(
            ContentValues().apply {
                put(COL_BODEGA_NOMBRE, "Galpón 2")
                put(COL_BODEGA_SECTOR, "Fundo Central")
            },
            ContentValues().apply {
                put(COL_BODEGA_NOMBRE, "Bodega Veterinaria")
                put(COL_BODEGA_SECTOR, "Enfermería")
            }
        )
        bodegas.forEach { db.insert(TABLE_BODEGA, null, it) }

        // Seed categorias
        val categorias = listOf(
            ContentValues().apply {
                put(COL_CATEGORIA_NOMBRE, "Forrajes")
                put(COL_CATEGORIA_EXIGE_TRAZABILIDAD, 0)
            },
            ContentValues().apply {
                put(COL_CATEGORIA_NOMBRE, "Sales")
                put(COL_CATEGORIA_EXIGE_TRAZABILIDAD, 0)
            },
            ContentValues().apply {
                put(COL_CATEGORIA_NOMBRE, "Fármacos")
                put(COL_CATEGORIA_EXIGE_TRAZABILIDAD, 1)
            },
            ContentValues().apply {
                put(COL_CATEGORIA_NOMBRE, "Fertilizantes")
                put(COL_CATEGORIA_EXIGE_TRAZABILIDAD, 0)
            }
        )
        categorias.forEach { db.insert(TABLE_CATEGORIA, null, it) }

        // Seed animales
        val animales = listOf(
            ContentValues().apply {
                put(COL_ANIMAL_ID, UUID.randomUUID().toString())
                put(COL_ANIMAL_DIIO, "123.456.789")
                put(COL_ANIMAL_ESTADO, "Vaca 402 (Lactancia Activa)")
            },
            ContentValues().apply {
                put(COL_ANIMAL_ID, UUID.randomUUID().toString())
                put(COL_ANIMAL_DIIO, "123.456.790")
                put(COL_ANIMAL_ESTADO, "Vaca 405 (Secado)")
            }
        )
        animales.forEach { db.insert(TABLE_ANIMAL, null, it) }

        // Seed initial movimientos
        val movimientos = listOf(
            ContentValues().apply {
                put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
                put(COL_MOVIMIENTO_TX_LOCAL, "TX-2024-0081")
                put(COL_MOVIMIENTO_CANTIDAD, 15)
                put(COL_MOVIMIENTO_DIIO, null as String?)
                put(COL_MOVIMIENTO_REQUIERE_DIIO, 0)
                put(COL_MOVIMIENTO_ESTADO_SYNC, "SYNC_PENDING")
                put(COL_MOVIMIENTO_TIME_LOCATION, "15 sacos de Concentrado · Hace 10 min · Galpón 2")
                put(COL_MOVIMIENTO_CREADO_EN, "2026-11-16T10:00:00Z")
            },
            ContentValues().apply {
                put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
                put(COL_MOVIMIENTO_TX_LOCAL, "TX-2024-0080")
                put(COL_MOVIMIENTO_CANTIDAD, 2)
                put(COL_MOVIMIENTO_DIIO, "DIIO: 123.456.789")
                put(COL_MOVIMIENTO_REQUIERE_DIIO, 1)
                put(COL_MOVIMIENTO_ESTADO_SYNC, "SYNC_PENDING")
                put(COL_MOVIMIENTO_TIME_LOCATION, "2 fármacos (Penicilina Bov 100ml) · Hace 25 min · Enfermería")
                put(COL_MOVIMIENTO_CREADO_EN, "2026-11-16T10:15:00Z")
            },
            ContentValues().apply {
                put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
                put(COL_MOVIMIENTO_TX_LOCAL, "TX-2024-0079")
                put(COL_MOVIMIENTO_CANTIDAD, 50)
                put(COL_MOVIMIENTO_DIIO, null as String?)
                put(COL_MOVIMIENTO_REQUIERE_DIIO, 0)
                put(COL_MOVIMIENTO_ESTADO_SYNC, "SYNC_PENDING")
                put(COL_MOVIMIENTO_TIME_LOCATION, "50 kg Sal Mineral Bloque · Hace 40 min · Potrero 4")
                put(COL_MOVIMIENTO_CREADO_EN, "2026-11-16T10:30:00Z")
            },
            ContentValues().apply {
                put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
                put(COL_MOVIMIENTO_TX_LOCAL, "TX-2024-0078")
                put(COL_MOVIMIENTO_CANTIDAD, 10)
                put(COL_MOVIMIENTO_DIIO, null as String?)
                put(COL_MOVIMIENTO_REQUIERE_DIIO, 0)
                put(COL_MOVIMIENTO_ESTADO_SYNC, "SYNC_PENDING")
                put(COL_MOVIMIENTO_TIME_LOCATION, "10 sacos Fertilizante Urea · Hace 1 hora · Bodega Central")
                put(COL_MOVIMIENTO_CREADO_EN, "2026-11-16T11:00:00Z")
            },
            ContentValues().apply {
                put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
                put(COL_MOVIMIENTO_TX_LOCAL, "TX-2024-0077")
                put(COL_MOVIMIENTO_CANTIDAD, 1)
                put(COL_MOVIMIENTO_DIIO, null as String?)
                put(COL_MOVIMIENTO_REQUIERE_DIIO, 0)
                put(COL_MOVIMIENTO_ESTADO_SYNC, "SYNC_PENDING")
                put(COL_MOVIMIENTO_TIME_LOCATION, "1 frasco Ivermectina 500ml · Hace 2 horas · Bodega Vivero")
                put(COL_MOVIMIENTO_CREADO_EN, "2026-11-16T12:00:00Z")
            }
        )
        movimientos.forEach { db.insert(TABLE_MOVIMIENTO, null, it) }
    }

    // --- USUARIO OPERATIONS ---

    fun insertUsuario(user: UserAccount): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_USUARIO_ID, UUID.randomUUID().toString())
            put(COL_USUARIO_RUT, user.rut)
            put(COL_USUARIO_EMAIL, user.rut + "@agrostock.cl")
            put(COL_USUARIO_PIN, user.pin)
            put(COL_USUARIO_ACTIVO, 1)
        }
        val result = db.insertWithOnConflict(TABLE_USUARIO, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun authenticateUsuario(rut: String, pin: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIO,
            arrayOf(COL_USUARIO_RUT),
            "$COL_USUARIO_RUT = ? AND $COL_USUARIO_PIN = ?",
            arrayOf(rut, pin),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun usuarioExists(rut: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIO,
            arrayOf(COL_USUARIO_RUT),
            "$COL_USUARIO_RUT = ?",
            arrayOf(rut),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // --- MOVIMIENTO OPERATIONS ---

    fun insertMovimiento(tx: OfflineTransaction, cantidad: Int = 1, requiereDiio: Boolean = false): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MOVIMIENTO_ID, UUID.randomUUID().toString())
            put(COL_MOVIMIENTO_TX_LOCAL, tx.id)
            put(COL_MOVIMIENTO_CANTIDAD, cantidad)
            put(COL_MOVIMIENTO_DIIO, tx.diio)
            put(COL_MOVIMIENTO_REQUIERE_DIIO, if (requiereDiio) 1 else 0)
            put(COL_MOVIMIENTO_ESTADO_SYNC, if (tx.isSynced) "PROCESADO" else "SYNC_PENDING")
            put(COL_MOVIMIENTO_TIME_LOCATION, tx.title + " · " + tx.timeLocation)
            put(COL_MOVIMIENTO_CREADO_EN, System.currentTimeMillis().toString())
        }
        val result = db.insertWithOnConflict(
            TABLE_MOVIMIENTO,
            null,
            cv,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getAllMovimientos(): List<OfflineTransaction> {
        val list = mutableListOf<OfflineTransaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOVIMIENTO ORDER BY rowid DESC", null)

        if (cursor.moveToFirst()) {
            val txLocalIdx = cursor.getColumnIndexOrThrow(COL_MOVIMIENTO_TX_LOCAL)
            val timeLocIdx = cursor.getColumnIndexOrThrow(COL_MOVIMIENTO_TIME_LOCATION)
            val diioIdx = cursor.getColumnIndexOrThrow(COL_MOVIMIENTO_DIIO)
            val syncIdx = cursor.getColumnIndexOrThrow(COL_MOVIMIENTO_ESTADO_SYNC)

            do {
                val fullTitleLoc = cursor.getString(timeLocIdx)
                val parts = fullTitleLoc.split(" · ")
                val title = parts.getOrNull(0) ?: fullTitleLoc
                val location = if (parts.size > 1) parts.drop(1).joinToString(" · ") else "Módulo Fundo"
                val syncStatus = cursor.getString(syncIdx)

                list.add(
                    OfflineTransaction(
                        id = cursor.getString(txLocalIdx),
                        title = title,
                        timeLocation = location,
                        diio = if (cursor.isNull(diioIdx)) null else cursor.getString(diioIdx),
                        isSynced = syncStatus == "PROCESADO" || syncStatus == "RECIBIDO"
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun markAllMovimientosSynced() {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MOVIMIENTO_ESTADO_SYNC, "PROCESADO")
        }
        db.update(TABLE_MOVIMIENTO, cv, null, null)
    }
}
