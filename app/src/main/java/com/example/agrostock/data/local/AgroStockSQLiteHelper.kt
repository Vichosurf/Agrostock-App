package com.example.agrostock.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction
import java.util.UUID

/**
 * Native Android SQLite Database Helper for AgroStock App.
 * Implements the official 10 relational SQL tables:
 * rol, usuario, bodega, categoria, producto, animal, proveedor, movimiento, registro_auditoria, sync_outbox.
 */
class AgroStockSQLiteHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "agrostock_database.db"
        private const val DATABASE_VERSION = 4

        // 1. rol
        private const val TABLE_ROL = "rol"
        private const val C_ROL_ID = "id_rol"
        private const val C_ROL_NOMBRE = "nombre_rol"
        private const val C_ROL_DESC = "descripcion"

        // 2. usuario
        private const val TABLE_USUARIO = "usuario"
        private const val C_USUARIO_ID = "id_usuario"
        private const val C_USUARIO_ID_ROL = "id_rol"
        private const val C_USUARIO_IDENTIFICADOR = "identificador" // RUT
        private const val C_USUARIO_NOMBRE = "nombre_completo"
        private const val C_USUARIO_PIN_HASH = "pin_hash"
        private const val C_USUARIO_ACTIVO = "activo"
        private const val C_USUARIO_CREADO = "creado_en"

        // 3. bodega
        private const val TABLE_BODEGA = "bodega"
        private const val C_BODEGA_ID = "id_bodega"
        private const val C_BODEGA_NOMBRE = "nombre_ubicacion"
        private const val C_BODEGA_SECTOR = "sector_predio"
        private const val C_BODEGA_ACTIVO = "activo"

        // 4. categoria
        private const val TABLE_CATEGORIA = "categoria"
        private const val C_CATEGORIA_ID = "id_categoria"
        private const val C_CATEGORIA_NOMBRE = "nombre_categoria"
        private const val C_CATEGORIA_TRAZABILIDAD = "exige_trazabilidad"

        // 5. producto
        private const val TABLE_PRODUCTO = "producto"
        private const val C_PRODUCTO_ID = "id_producto"
        private const val C_PRODUCTO_ID_BODEGA = "id_bodega"
        private const val C_PRODUCTO_ID_CATEGORIA = "id_categoria"
        private const val C_PRODUCTO_SKU = "codigo_sku"
        private const val C_PRODUCTO_DESC = "descripcion"
        private const val C_PRODUCTO_STOCK_ACTUAL = "stock_actual"
        private const val C_PRODUCTO_STOCK_MIN = "stock_minimo"
        private const val C_PRODUCTO_UNIDAD = "unidad_medida"
        private const val C_PRODUCTO_ACTIVO = "activo"

        // 6. animal
        private const val TABLE_ANIMAL = "animal"
        private const val C_ANIMAL_ID = "id_animal"
        private const val C_ANIMAL_DIIO = "diio"
        private const val C_ANIMAL_ESTADO = "estado"

        // 7. proveedor
        private const val TABLE_PROVEEDOR = "proveedor"
        private const val C_PROVEEDOR_ID = "id_proveedor"
        private const val C_PROVEEDOR_IDENTIFICADOR = "identificador"
        private const val C_PROVEEDOR_RAZON_SOCIAL = "razon_social"
        private const val C_PROVEEDOR_ACTIVO = "activo"

        // 8. movimiento
        private const val TABLE_MOVIMIENTO = "movimiento"
        private const val C_MOV_ID = "id_movimiento"
        private const val C_MOV_TX_LOCAL = "id_transaccion_local"
        private const val C_MOV_ID_USUARIO = "id_usuario"
        private const val C_MOV_ID_PRODUCTO = "id_producto"
        private const val C_MOV_ID_ANIMAL = "id_animal"
        private const val C_MOV_DIIO = "diio"
        private const val C_MOV_ID_PROVEEDOR = "id_proveedor"
        private const val C_MOV_TIPO_OP = "tipo_operacion"
        private const val C_MOV_CANTIDAD = "cantidad"
        private const val C_MOV_REQUIERE_DIIO = "requiere_diio"
        private const val C_MOV_DOC_REF = "documento_referencia"
        private const val C_MOV_FECHA_CLIENTE = "fecha_hora_cliente"
        private const val C_MOV_RECIBIDO_EN = "recibido_en"
        private const val C_MOV_ESTADO_SYNC = "estado_sincronizacion"

        // 9. registro_auditoria
        private const val TABLE_AUDITORIA = "registro_auditoria"
        private const val C_AUD_ID = "id_auditoria"
        private const val C_AUD_ID_USUARIO = "id_usuario"
        private const val C_AUD_ACCION = "accion"
        private const val C_AUD_ENTIDAD = "entidad"
        private const val C_AUD_ID_ENTIDAD = "id_entidad"
        private const val C_AUD_CREADO_EN = "creado_en"
        private const val C_AUD_DETALLE = "detalle"

        // 10. sync_outbox
        private const val TABLE_OUTBOX = "sync_outbox"
        private const val C_BOX_ID_EVENTO = "id_evento"
        private const val C_BOX_ID_MOVIMIENTO = "id_movimiento"
        private const val C_BOX_TX_LOCAL = "id_transaccion_local"
        private const val C_BOX_DOC_JSON = "documento_json"
        private const val C_BOX_ESTADO = "estado"
        private const val C_BOX_INTENTOS = "intentos"
        private const val C_BOX_CREADO_EN = "creado_en"
        private const val C_BOX_PROCESADO_EN = "procesado_en"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_ROL (
                $C_ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_ROL_NOMBRE TEXT NOT NULL UNIQUE,
                $C_ROL_DESC TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_USUARIO (
                $C_USUARIO_ID TEXT PRIMARY KEY,
                $C_USUARIO_ID_ROL INTEGER NOT NULL REFERENCES $TABLE_ROL($C_ROL_ID),
                $C_USUARIO_IDENTIFICADOR TEXT NOT NULL UNIQUE,
                $C_USUARIO_NOMBRE TEXT NOT NULL,
                $C_USUARIO_PIN_HASH TEXT NOT NULL,
                $C_USUARIO_ACTIVO INTEGER NOT NULL DEFAULT 1,
                $C_USUARIO_CREADO TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_BODEGA (
                $C_BODEGA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_BODEGA_NOMBRE TEXT NOT NULL,
                $C_BODEGA_SECTOR TEXT,
                $C_BODEGA_ACTIVO INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_CATEGORIA (
                $C_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_CATEGORIA_NOMBRE TEXT NOT NULL UNIQUE,
                $C_CATEGORIA_TRAZABILIDAD INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_PRODUCTO (
                $C_PRODUCTO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_PRODUCTO_ID_BODEGA INTEGER NOT NULL REFERENCES $TABLE_BODEGA($C_BODEGA_ID),
                $C_PRODUCTO_ID_CATEGORIA INTEGER NOT NULL REFERENCES $TABLE_CATEGORIA($C_CATEGORIA_ID),
                $C_PRODUCTO_SKU TEXT NOT NULL UNIQUE,
                $C_PRODUCTO_DESC TEXT NOT NULL,
                $C_PRODUCTO_STOCK_ACTUAL REAL NOT NULL DEFAULT 0 CHECK ($C_PRODUCTO_STOCK_ACTUAL >= 0),
                $C_PRODUCTO_STOCK_MIN REAL NOT NULL DEFAULT 0 CHECK ($C_PRODUCTO_STOCK_MIN >= 0),
                $C_PRODUCTO_UNIDAD TEXT NOT NULL,
                $C_PRODUCTO_ACTIVO INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ANIMAL (
                $C_ANIMAL_ID TEXT PRIMARY KEY,
                $C_ANIMAL_DIIO TEXT NOT NULL UNIQUE,
                $C_ANIMAL_ESTADO TEXT NOT NULL DEFAULT 'ACTIVO'
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_PROVEEDOR (
                $C_PROVEEDOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_PROVEEDOR_IDENTIFICADOR TEXT NOT NULL UNIQUE,
                $C_PROVEEDOR_RAZON_SOCIAL TEXT NOT NULL,
                $C_PROVEEDOR_ACTIVO INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_MOVIMIENTO (
                $C_MOV_ID TEXT PRIMARY KEY,
                $C_MOV_TX_LOCAL TEXT NOT NULL UNIQUE,
                $C_MOV_ID_USUARIO TEXT NOT NULL REFERENCES $TABLE_USUARIO($C_USUARIO_ID),
                $C_MOV_ID_PRODUCTO INTEGER NOT NULL REFERENCES $TABLE_PRODUCTO($C_PRODUCTO_ID),
                $C_MOV_ID_ANIMAL TEXT REFERENCES $TABLE_ANIMAL($C_ANIMAL_ID),
                $C_MOV_DIIO TEXT,
                $C_MOV_ID_PROVEEDOR INTEGER REFERENCES $TABLE_PROVEEDOR($C_PROVEEDOR_ID),
                $C_MOV_TIPO_OP TEXT NOT NULL DEFAULT 'EGRESO',
                $C_MOV_CANTIDAD REAL NOT NULL CHECK ($C_MOV_CANTIDAD > 0),
                $C_MOV_REQUIERE_DIIO INTEGER NOT NULL DEFAULT 0,
                $C_MOV_DOC_REF TEXT,
                $C_MOV_FECHA_CLIENTE TEXT,
                $C_MOV_RECIBIDO_EN TEXT NOT NULL,
                $C_MOV_ESTADO_SYNC TEXT NOT NULL DEFAULT 'RECIBIDO'
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_AUDITORIA (
                $C_AUD_ID TEXT PRIMARY KEY,
                $C_AUD_ID_USUARIO TEXT NOT NULL REFERENCES $TABLE_USUARIO($C_USUARIO_ID),
                $C_AUD_ACCION TEXT NOT NULL,
                $C_AUD_ENTIDAD TEXT NOT NULL,
                $C_AUD_ID_ENTIDAD TEXT,
                $C_AUD_CREADO_EN TEXT NOT NULL,
                $C_AUD_DETALLE TEXT NOT NULL DEFAULT '{}'
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_OUTBOX (
                $C_BOX_ID_EVENTO TEXT PRIMARY KEY,
                $C_BOX_ID_MOVIMIENTO TEXT REFERENCES $TABLE_MOVIMIENTO($C_MOV_ID),
                $C_BOX_TX_LOCAL TEXT NOT NULL,
                $C_BOX_DOC_JSON TEXT NOT NULL,
                $C_BOX_ESTADO TEXT NOT NULL DEFAULT 'PENDIENTE',
                $C_BOX_INTENTOS INTEGER NOT NULL DEFAULT 0,
                $C_BOX_CREADO_EN TEXT NOT NULL,
                $C_BOX_PROCESADO_EN TEXT
            )
        """.trimIndent())

        seedOfficialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OUTBOX")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUDITORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIMIENTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROVEEDOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BODEGA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROL")
        onCreate(db)
    }

    private fun seedOfficialData(db: SQLiteDatabase) {
        // 1. Roles
        val idRolOp = db.insert(TABLE_ROL, null, ContentValues().apply {
            put(C_ROL_NOMBRE, "OPERARIO_SANITARIO")
            put(C_ROL_DESC, "Operario de terreno para retiros de insumos")
        })

        // 2. Usuarios
        val defaultUserUuid = UUID.randomUUID().toString()
        val users = listOf(
            ContentValues().apply {
                put(C_USUARIO_ID, defaultUserUuid)
                put(C_USUARIO_ID_ROL, idRolOp)
                put(C_USUARIO_IDENTIFICADOR, "12.345.678-5")
                put(C_USUARIO_NOMBRE, "Operario General")
                put(C_USUARIO_PIN_HASH, "123456")
                put(C_USUARIO_ACTIVO, 1)
                put(C_USUARIO_CREADO, System.currentTimeMillis().toString())
            },
            ContentValues().apply {
                put(C_USUARIO_ID, UUID.randomUUID().toString())
                put(C_USUARIO_ID_ROL, idRolOp)
                put(C_USUARIO_IDENTIFICADOR, "5.108.566-3")
                put(C_USUARIO_NOMBRE, "Operario Sanitario Moncopulli")
                put(C_USUARIO_PIN_HASH, "654321")
                put(C_USUARIO_ACTIVO, 1)
                put(C_USUARIO_CREADO, System.currentTimeMillis().toString())
            }
        )
        users.forEach { db.insert(TABLE_USUARIO, null, it) }

        // 3. Bodegas
        val idBodega1 = db.insert(TABLE_BODEGA, null, ContentValues().apply {
            put(C_BODEGA_NOMBRE, "Galpón Central")
            put(C_BODEGA_SECTOR, "Fundo Moncopulli")
        })

        // 4. Categorias
        val idCatForrajes = db.insert(TABLE_CATEGORIA, null, ContentValues().apply {
            put(C_CATEGORIA_NOMBRE, "Forrajes")
            put(C_CATEGORIA_TRAZABILIDAD, 0)
        })
        val idCatFarmacos = db.insert(TABLE_CATEGORIA, null, ContentValues().apply {
            put(C_CATEGORIA_NOMBRE, "Fármacos")
            put(C_CATEGORIA_TRAZABILIDAD, 1)
        })

        // 5. Productos
        db.insert(TABLE_PRODUCTO, null, ContentValues().apply {
            put(C_PRODUCTO_ID_BODEGA, idBodega1)
            put(C_PRODUCTO_ID_CATEGORIA, idCatForrajes)
            put(C_PRODUCTO_SKU, "FOR-CONC-001")
            put(C_PRODUCTO_DESC, "Saco de Concentrado 40KG")
            put(C_PRODUCTO_STOCK_ACTUAL, 124)
            put(C_PRODUCTO_STOCK_MIN, 20)
            put(C_PRODUCTO_UNIDAD, "sacos")
        })

        db.insert(TABLE_PRODUCTO, null, ContentValues().apply {
            put(C_PRODUCTO_ID_BODEGA, idBodega1)
            put(C_PRODUCTO_ID_CATEGORIA, idCatFarmacos)
            put(C_PRODUCTO_SKU, "FAR-PENI-002")
            put(C_PRODUCTO_DESC, "Penicilina Retard 100ml")
            put(C_PRODUCTO_STOCK_ACTUAL, 24)
            put(C_PRODUCTO_STOCK_MIN, 5)
            put(C_PRODUCTO_UNIDAD, "frascos")
        })

        // 6. Animales
        val animUuid = UUID.randomUUID().toString()
        db.insert(TABLE_ANIMAL, null, ContentValues().apply {
            put(C_ANIMAL_ID, animUuid)
            put(C_ANIMAL_DIIO, "123.456.789")
            put(C_ANIMAL_ESTADO, "ACTIVO")
        })

        // Seed initial sync outbox & movimientos
        val movTxId = "TX-2024-0081"
        val movUuid = UUID.randomUUID().toString()
        val mov = ContentValues().apply {
            put(C_MOV_ID, movUuid)
            put(C_MOV_TX_LOCAL, movTxId)
            put(C_MOV_ID_USUARIO, defaultUserUuid)
            put(C_MOV_ID_PRODUCTO, 1)
            put(C_MOV_TIPO_OP, "EGRESO")
            put(C_MOV_CANTIDAD, 15)
            put(C_MOV_REQUIERE_DIIO, 0)
            put(C_MOV_FECHA_CLIENTE, "15 sacos de Concentrado · Hace 10 min · Galpón 2")
            put(C_MOV_RECIBIDO_EN, System.currentTimeMillis().toString())
            put(C_MOV_ESTADO_SYNC, "RECIBIDO")
        }
        db.insert(TABLE_MOVIMIENTO, null, mov)

        val outbox = ContentValues().apply {
            put(C_BOX_ID_EVENTO, UUID.randomUUID().toString())
            put(C_BOX_ID_MOVIMIENTO, movUuid)
            put(C_BOX_TX_LOCAL, movTxId)
            put(C_BOX_DOC_JSON, "{\"id_transaccion_local\":\"$movTxId\",\"cantidad\":15}")
            put(C_BOX_ESTADO, "PENDIENTE")
            put(C_BOX_CREADO_EN, System.currentTimeMillis().toString())
        }
        db.insert(TABLE_OUTBOX, null, outbox)
    }

    // --- USUARIO OPERATIONS ---

    fun insertUsuario(user: UserAccount): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(C_USUARIO_ID, UUID.randomUUID().toString())
            put(C_USUARIO_ID_ROL, 1)
            put(C_USUARIO_IDENTIFICADOR, user.rut)
            put(C_USUARIO_NOMBRE, "Operario " + user.rut)
            put(C_USUARIO_PIN_HASH, user.pin)
            put(C_USUARIO_ACTIVO, 1)
            put(C_USUARIO_CREADO, System.currentTimeMillis().toString())
        }
        val result = db.insertWithOnConflict(TABLE_USUARIO, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun authenticateUsuario(rut: String, pin: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIO,
            arrayOf(C_USUARIO_IDENTIFICADOR),
            "$C_USUARIO_IDENTIFICADOR = ? AND $C_USUARIO_PIN_HASH = ?",
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
            arrayOf(C_USUARIO_IDENTIFICADOR),
            "$C_USUARIO_IDENTIFICADOR = ?",
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
        val movUuid = UUID.randomUUID().toString()
        val eventUuid = UUID.randomUUID().toString()

        val cvMov = ContentValues().apply {
            put(C_MOV_ID, movUuid)
            put(C_MOV_TX_LOCAL, tx.id)
            put(C_MOV_ID_USUARIO, UUID.randomUUID().toString())
            put(C_MOV_ID_PRODUCTO, 1)
            put(C_MOV_DIIO, tx.diio)
            put(C_MOV_CANTIDAD, cantidad)
            put(C_MOV_REQUIERE_DIIO, if (requiereDiio) 1 else 0)
            put(C_MOV_FECHA_CLIENTE, tx.title + " · " + tx.timeLocation)
            put(C_MOV_RECIBIDO_EN, System.currentTimeMillis().toString())
            put(C_MOV_ESTADO_SYNC, if (tx.isSynced) "PROCESADO" else "RECIBIDO")
        }
        db.insertWithOnConflict(TABLE_MOVIMIENTO, null, cvMov, SQLiteDatabase.CONFLICT_REPLACE)

        val cvBox = ContentValues().apply {
            put(C_BOX_ID_EVENTO, eventUuid)
            put(C_BOX_ID_MOVIMIENTO, movUuid)
            put(C_BOX_TX_LOCAL, tx.id)
            put(C_BOX_DOC_JSON, "{\"id_transaccion_local\":\"${tx.id}\",\"cantidad\":$cantidad,\"diio\":\"${tx.diio ?: ""}\"}")
            put(C_BOX_ESTADO, if (tx.isSynced) "ENVIADO" else "PENDIENTE")
            put(C_BOX_CREADO_EN, System.currentTimeMillis().toString())
        }
        val result = db.insertWithOnConflict(TABLE_OUTBOX, null, cvBox, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun getAllMovimientos(): List<OfflineTransaction> {
        val list = mutableListOf<OfflineTransaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOVIMIENTO ORDER BY rowid DESC", null)

        if (cursor.moveToFirst()) {
            val txLocalIdx = cursor.getColumnIndexOrThrow(C_MOV_TX_LOCAL)
            val timeLocIdx = cursor.getColumnIndexOrThrow(C_MOV_FECHA_CLIENTE)
            val diioIdx = cursor.getColumnIndexOrThrow(C_MOV_DIIO)
            val syncIdx = cursor.getColumnIndexOrThrow(C_MOV_ESTADO_SYNC)

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
                        isSynced = syncStatus == "PROCESADO"
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun markAllMovimientosSynced() {
        val db = writableDatabase
        val cvMov = ContentValues().apply {
            put(C_MOV_ESTADO_SYNC, "PROCESADO")
        }
        db.update(TABLE_MOVIMIENTO, cvMov, null, null)

        val cvBox = ContentValues().apply {
            put(C_BOX_ESTADO, "ENVIADO")
            put(C_BOX_PROCESADO_EN, System.currentTimeMillis().toString())
        }
        db.update(TABLE_OUTBOX, cvBox, null, null)
    }
}
