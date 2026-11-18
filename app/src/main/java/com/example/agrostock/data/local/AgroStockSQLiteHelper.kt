package com.example.agrostock.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.agrostock.data.InventoryProduct
import com.example.agrostock.data.PendingSyncEvent
import com.example.agrostock.data.UserAccount
import com.example.agrostock.ui.screens.OfflineTransaction
import com.example.agrostock.util.PinHasher
import org.json.JSONObject
import java.text.DateFormat
import java.util.Date
import java.util.UUID

/** Persistencia SQLite local; la migración v4→v5 conserva registros existentes. */
class AgroStockSQLiteHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "agrostock_database.db"
        private const val DATABASE_VERSION = 5
        private const val TABLE_ROL = "rol"
        private const val TABLE_USUARIO = "usuario"
        private const val TABLE_BODEGA = "bodega"
        private const val TABLE_CATEGORIA = "categoria"
        private const val TABLE_PRODUCTO = "producto"
        private const val TABLE_ANIMAL = "animal"
        private const val TABLE_PROVEEDOR = "proveedor"
        private const val TABLE_MOVIMIENTO = "movimiento"
        private const val TABLE_AUDITORIA = "registro_auditoria"
        private const val TABLE_OUTBOX = "sync_outbox"

        private const val C_ROL_ID = "id_rol"
        private const val C_ROL_NOMBRE = "nombre_rol"
        private const val C_ROL_DESC = "descripcion"
        private const val C_USUARIO_ID = "id_usuario"
        private const val C_USUARIO_ID_ROL = "id_rol"
        private const val C_USUARIO_IDENTIFICADOR = "identificador"
        private const val C_USUARIO_NOMBRE = "nombre_completo"
        private const val C_USUARIO_PIN_HASH = "pin_hash"
        private const val C_USUARIO_ACTIVO = "activo"
        private const val C_USUARIO_CREADO = "creado_en"
        private const val C_BODEGA_NOMBRE = "nombre_ubicacion"
        private const val C_BODEGA_SECTOR = "sector_predio"
        private const val C_CATEGORIA_ID = "id_categoria"
        private const val C_CATEGORIA_NOMBRE = "nombre_categoria"
        private const val C_CATEGORIA_TRAZABILIDAD = "exige_trazabilidad"
        private const val C_PRODUCTO_ID = "id_producto"
        private const val C_PRODUCTO_ID_BODEGA = "id_bodega"
        private const val C_PRODUCTO_ID_CATEGORIA = "id_categoria"
        private const val C_PRODUCTO_SKU = "codigo_sku"
        private const val C_PRODUCTO_DESC = "descripcion"
        private const val C_PRODUCTO_STOCK_ACTUAL = "stock_actual"
        private const val C_PRODUCTO_STOCK_MIN = "stock_minimo"
        private const val C_PRODUCTO_UNIDAD = "unidad_medida"
        private const val C_PRODUCTO_ACTIVO = "activo"
        private const val C_ANIMAL_ID = "id_animal"
        private const val C_ANIMAL_DIIO = "diio"
        private const val C_ANIMAL_ESTADO = "estado"
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
        private const val C_BOX_ID_EVENTO = "id_evento"
        private const val C_BOX_ID_MOVIMIENTO = "id_movimiento"
        private const val C_BOX_TX_LOCAL = "id_transaccion_local"
        private const val C_BOX_DOC_JSON = "documento_json"
        private const val C_BOX_ESTADO = "estado"
        private const val C_BOX_INTENTOS = "intentos"
        private const val C_BOX_CREADO_EN = "creado_en"
        private const val C_BOX_PROCESADO_EN = "procesado_en"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createSchema(db)
        seedReferenceData(db)
        createIndexes(db)
    }

    private fun createSchema(db: SQLiteDatabase) {
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
                id_bodega INTEGER PRIMARY KEY AUTOINCREMENT,
                $C_BODEGA_NOMBRE TEXT NOT NULL,
                $C_BODEGA_SECTOR TEXT,
                activo INTEGER NOT NULL DEFAULT 1
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
                $C_PRODUCTO_ID_BODEGA INTEGER NOT NULL REFERENCES $TABLE_BODEGA(id_bodega),
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
                    CHECK ($C_ANIMAL_ESTADO IN ('ACTIVO', 'INACTIVO'))
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE $TABLE_PROVEEDOR (
                id_proveedor INTEGER PRIMARY KEY AUTOINCREMENT,
                identificador TEXT NOT NULL UNIQUE,
                razon_social TEXT NOT NULL,
                activo INTEGER NOT NULL DEFAULT 1
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
                $C_MOV_ID_PROVEEDOR INTEGER REFERENCES $TABLE_PROVEEDOR(id_proveedor),
                $C_MOV_TIPO_OP TEXT NOT NULL DEFAULT 'EGRESO'
                    CHECK ($C_MOV_TIPO_OP IN ('INGRESO', 'EGRESO', 'AJUSTE')),
                $C_MOV_CANTIDAD REAL NOT NULL CHECK ($C_MOV_CANTIDAD > 0),
                $C_MOV_REQUIERE_DIIO INTEGER NOT NULL DEFAULT 0,
                $C_MOV_DOC_REF TEXT,
                $C_MOV_FECHA_CLIENTE TEXT NOT NULL,
                $C_MOV_RECIBIDO_EN TEXT NOT NULL,
                $C_MOV_ESTADO_SYNC TEXT NOT NULL DEFAULT 'RECIBIDO'
                    CHECK ($C_MOV_ESTADO_SYNC IN ('RECIBIDO', 'PROCESADO', 'RECHAZADO')),
                CHECK ($C_MOV_REQUIERE_DIIO = 0 OR $C_MOV_ID_ANIMAL IS NOT NULL)
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE $TABLE_AUDITORIA (
                id_auditoria TEXT PRIMARY KEY,
                id_usuario TEXT NOT NULL REFERENCES $TABLE_USUARIO($C_USUARIO_ID),
                accion TEXT NOT NULL,
                entidad TEXT NOT NULL,
                id_entidad TEXT,
                creado_en TEXT NOT NULL,
                detalle TEXT NOT NULL DEFAULT '{}'
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE $TABLE_OUTBOX (
                $C_BOX_ID_EVENTO TEXT PRIMARY KEY,
                $C_BOX_ID_MOVIMIENTO TEXT REFERENCES $TABLE_MOVIMIENTO($C_MOV_ID),
                $C_BOX_TX_LOCAL TEXT NOT NULL UNIQUE,
                $C_BOX_DOC_JSON TEXT NOT NULL,
                $C_BOX_ESTADO TEXT NOT NULL DEFAULT 'PENDIENTE'
                    CHECK ($C_BOX_ESTADO IN ('PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ERROR')),
                $C_BOX_INTENTOS INTEGER NOT NULL DEFAULT 0 CHECK ($C_BOX_INTENTOS >= 0),
                $C_BOX_CREADO_EN TEXT NOT NULL,
                $C_BOX_PROCESADO_EN TEXT
            )
        """.trimIndent())
    }

    private fun seedReferenceData(db: SQLiteDatabase) {
        db.insertOrThrow(TABLE_ROL, null, ContentValues().apply {
            put(C_ROL_NOMBRE, "OPERARIO_SANITARIO")
            put(C_ROL_DESC, "Operario de terreno para retiros de insumos")
        })
        val warehouseId = db.insertOrThrow(TABLE_BODEGA, null, ContentValues().apply {
            put(C_BODEGA_NOMBRE, "Galpón Central")
            put(C_BODEGA_SECTOR, "Fundo Moncopulli")
        })
        listOf(
            "Forrajes" to 0,
            "Sales" to 0,
            "Fármacos" to 1,
            "Fertilizantes" to 0
        ).forEach { (name, trace) ->
            db.insertOrThrow(TABLE_CATEGORIA, null, ContentValues().apply {
                put(C_CATEGORIA_NOMBRE, name)
                put(C_CATEGORIA_TRAZABILIDAD, trace)
            })
        }
        val products = listOf(
            Triple("FOR-CONC-001", "Saco de Concentrado 40KG", 124.0),
            Triple("SAL-MIN-001", "Sal Mineral", 48.0),
            Triple("FAR-PENI-002", "Penicilina Retard 100ml", 24.0),
            Triple("FER-UREA-001", "Fertilizante Urea", 90.0)
        )
        products.forEachIndexed { index, item ->
            db.insertOrThrow(TABLE_PRODUCTO, null, ContentValues().apply {
                put(C_PRODUCTO_ID_BODEGA, warehouseId)
                put(C_PRODUCTO_ID_CATEGORIA, index + 1)
                put(C_PRODUCTO_SKU, item.first)
                put(C_PRODUCTO_DESC, item.second)
                put(C_PRODUCTO_STOCK_ACTUAL, item.third)
                put(C_PRODUCTO_STOCK_MIN, if (index == 0) 20 else 5)
                put(C_PRODUCTO_UNIDAD, when (index) {
                    1 -> "bloques"
                    2 -> "frascos"
                    else -> "sacos"
                })
            })
        }
        db.insertOrThrow(TABLE_ANIMAL, null, ContentValues().apply {
            put(C_ANIMAL_ID, UUID.randomUUID().toString())
            put(C_ANIMAL_DIIO, "123.456.789")
            put(C_ANIMAL_ESTADO, "ACTIVO")
        })
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_movimiento_fecha ON $TABLE_MOVIMIENTO($C_MOV_RECIBIDO_EN DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_movimiento_producto_fecha ON $TABLE_MOVIMIENTO($C_MOV_ID_PRODUCTO, $C_MOV_RECIBIDO_EN DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_movimiento_usuario_fecha ON $TABLE_MOVIMIENTO($C_MOV_ID_USUARIO, $C_MOV_RECIBIDO_EN DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_outbox_estado_fecha ON $TABLE_OUTBOX($C_BOX_ESTADO, $C_BOX_CREADO_EN)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS ux_outbox_transaccion ON $TABLE_OUTBOX($C_BOX_TX_LOCAL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            throw IllegalStateException(
                "La base v$oldVersion necesita una migración específica. Se evita eliminar tablas automáticamente."
            )
        }
        db.beginTransaction()
        try {
            if (oldVersion < 5) migrateV4ToV5(db)
            createIndexes(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun migrateV4ToV5(db: SQLiteDatabase) {
        data class UserRow(val id: String, val pin: String, val rut: String, val name: String)
        val users = mutableListOf<UserRow>()
        db.query(
            TABLE_USUARIO,
            arrayOf(C_USUARIO_ID, C_USUARIO_PIN_HASH, C_USUARIO_IDENTIFICADOR, C_USUARIO_NOMBRE),
            null, null, null, null, null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                users += UserRow(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3))
            }
        }
        users.forEach { user ->
            if (!PinHasher.isEncoded(user.pin)) {
                db.update(TABLE_USUARIO,
                    ContentValues().apply { put(C_USUARIO_PIN_HASH, PinHasher.hash(user.pin)) },
                    "$C_USUARIO_ID = ?", arrayOf(user.id))
            }
            val seedUser =
                (user.rut == "12.345.678-5" && user.name == "Operario General") ||
                (user.rut == "5.108.566-3" && user.name == "Operario Sanitario Moncopulli")
            if (seedUser) {
                db.delete(TABLE_OUTBOX, "$C_BOX_TX_LOCAL = ?", arrayOf("TX-2024-0081"))
                db.delete(TABLE_MOVIMIENTO, "$C_MOV_TX_LOCAL = ?", arrayOf("TX-2024-0081"))
                db.delete(TABLE_USUARIO, "$C_USUARIO_ID = ?", arrayOf(user.id))
            }
        }
    }

    fun insertUsuario(user: UserAccount): Boolean {
        require(user.pin.length == 6 && user.pin.all { it.isDigit() }) {
            "El PIN debe contener exactamente seis dígitos."
        }
        val roleId = readableDatabase.query(
            TABLE_ROL, arrayOf(C_ROL_ID), "$C_ROL_NOMBRE = ?",
            arrayOf("OPERARIO_SANITARIO"), null, null, null
        ).use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else return false }
        val values = ContentValues().apply {
            put(C_USUARIO_ID, UUID.randomUUID().toString())
            put(C_USUARIO_ID_ROL, roleId)
            put(C_USUARIO_IDENTIFICADOR, user.rut)
            put(C_USUARIO_NOMBRE, "Operario " + user.rut)
            put(C_USUARIO_PIN_HASH, PinHasher.hash(user.pin))
            put(C_USUARIO_ACTIVO, 1)
            put(C_USUARIO_CREADO, System.currentTimeMillis().toString())
        }
        return writableDatabase.insertWithOnConflict(
            TABLE_USUARIO, null, values, SQLiteDatabase.CONFLICT_IGNORE
        ) != -1L
    }

    fun authenticateUsuario(rut: String, pin: String): Boolean {
        val storedHash = readableDatabase.query(
            TABLE_USUARIO, arrayOf(C_USUARIO_PIN_HASH),
            "$C_USUARIO_IDENTIFICADOR = ? AND $C_USUARIO_ACTIVO = 1",
            arrayOf(rut), null, null, null
        ).use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else return false }
        return PinHasher.verify(pin, storedHash)
    }

    fun usuarioExists(rut: String): Boolean = readableDatabase.query(
        TABLE_USUARIO, arrayOf(C_USUARIO_ID),
        "$C_USUARIO_IDENTIFICADOR = ?", arrayOf(rut), null, null, null
    ).use { it.moveToFirst() }

    fun fetchProducts(): List<InventoryProduct> {
        val products = mutableListOf<InventoryProduct>()
        readableDatabase.rawQuery(
            """
            SELECT p.$C_PRODUCTO_ID, p.$C_PRODUCTO_DESC, c.$C_CATEGORIA_NOMBRE,
                   p.$C_PRODUCTO_STOCK_ACTUAL, p.$C_PRODUCTO_UNIDAD,
                   c.$C_CATEGORIA_TRAZABILIDAD
            FROM $TABLE_PRODUCTO p
            INNER JOIN $TABLE_CATEGORIA c
              ON c.$C_CATEGORIA_ID = p.$C_PRODUCTO_ID_CATEGORIA
            WHERE p.$C_PRODUCTO_ACTIVO = 1
            ORDER BY c.$C_CATEGORIA_NOMBRE, p.$C_PRODUCTO_DESC
            """.trimIndent(), null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                products += InventoryProduct(
                    cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    cursor.getDouble(3), cursor.getString(4), cursor.getInt(5) == 1
                )
            }
        }
        return products
    }

    fun insertWithdrawal(rut: String, productId: Long, quantity: Int, diio: String?): String {
        require(quantity > 0) { "La cantidad debe ser mayor que cero." }
        val db = writableDatabase
        val movementId = UUID.randomUUID().toString()
        val transactionId = UUID.randomUUID().toString()
        val eventId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis().toString()
        db.beginTransaction()
        try {
            val userId = db.query(
                TABLE_USUARIO, arrayOf(C_USUARIO_ID),
                "$C_USUARIO_IDENTIFICADOR = ? AND $C_USUARIO_ACTIVO = 1",
                arrayOf(rut), null, null, null
            ).use { if (it.moveToFirst()) it.getString(0) else null }
                ?: throw IllegalArgumentException("El usuario no está activo. Inicia sesión nuevamente.")

            val product = db.rawQuery(
                """
                SELECT p.$C_PRODUCTO_STOCK_ACTUAL, p.$C_PRODUCTO_DESC, p.$C_PRODUCTO_UNIDAD,
                       c.$C_CATEGORIA_TRAZABILIDAD
                FROM $TABLE_PRODUCTO p
                INNER JOIN $TABLE_CATEGORIA c
                  ON c.$C_CATEGORIA_ID = p.$C_PRODUCTO_ID_CATEGORIA
                WHERE p.$C_PRODUCTO_ID = ? AND p.$C_PRODUCTO_ACTIVO = 1
                """.trimIndent(), arrayOf(productId.toString())
            ).use { cursor ->
                if (cursor.moveToFirst()) listOf(
                    cursor.getDouble(0).toString(), cursor.getString(1),
                    cursor.getString(2), (cursor.getInt(3) == 1).toString()
                ) else null
            } ?: throw IllegalArgumentException("El producto no existe o está inactivo.")

            val stock = product[0].toDouble()
            val productName = product[1]
            val unit = product[2]
            val requiresDiio = product[3].toBoolean()
            require(quantity.toDouble() <= stock) { "Stock insuficiente. Disponible: $stock $unit." }

            var animalId: String? = null
            var normalizedDiio: String? = null
            if (requiresDiio) {
                normalizedDiio = diio.orEmpty().filter { it.isDigit() }
                require(normalizedDiio.length == 9) { "Este producto exige un DIIO de nueve dígitos." }
                animalId = db.rawQuery(
                    """
                    SELECT $C_ANIMAL_ID FROM $TABLE_ANIMAL
                    WHERE replace($C_ANIMAL_DIIO, '.', '') = ? AND $C_ANIMAL_ESTADO = 'ACTIVO'
                    """.trimIndent(), arrayOf(normalizedDiio)
                ).use { if (it.moveToFirst()) it.getString(0) else null }
                require(animalId != null) { "El DIIO no existe o el animal no está activo." }
            }

            val stockUpdated = db.update(
                TABLE_PRODUCTO,
                ContentValues().apply { put(C_PRODUCTO_STOCK_ACTUAL, stock - quantity) },
                "$C_PRODUCTO_ID = ? AND $C_PRODUCTO_STOCK_ACTUAL >= ?",
                arrayOf(productId.toString(), quantity.toString())
            )
            check(stockUpdated == 1) { "No fue posible reservar el stock solicitado." }

            val movement = ContentValues().apply {
                put(C_MOV_ID, movementId)
                put(C_MOV_TX_LOCAL, transactionId)
                put(C_MOV_ID_USUARIO, userId)
                put(C_MOV_ID_PRODUCTO, productId)
                if (animalId == null) putNull(C_MOV_ID_ANIMAL) else put(C_MOV_ID_ANIMAL, animalId)
                if (normalizedDiio == null) putNull(C_MOV_DIIO) else put(C_MOV_DIIO, normalizedDiio)
                put(C_MOV_TIPO_OP, "EGRESO")
                put(C_MOV_CANTIDAD, quantity.toDouble())
                put(C_MOV_REQUIERE_DIIO, if (requiresDiio) 1 else 0)
                put(C_MOV_FECHA_CLIENTE, now)
                put(C_MOV_RECIBIDO_EN, now)
                put(C_MOV_ESTADO_SYNC, "RECIBIDO")
            }
            db.insertOrThrow(TABLE_MOVIMIENTO, null, movement)

            val payload = JSONObject()
                .put("id_transaccion_local", transactionId)
                .put("id_usuario", userId)
                .put("id_producto", productId)
                .put("tipo_operacion", "EGRESO")
                .put("cantidad", quantity)
                .put("requiere_diio", requiresDiio)
                .put("id_animal", animalId)
            val outbox = ContentValues().apply {
                put(C_BOX_ID_EVENTO, eventId)
                put(C_BOX_ID_MOVIMIENTO, movementId)
                put(C_BOX_TX_LOCAL, transactionId)
                put(C_BOX_DOC_JSON, payload.toString())
                put(C_BOX_ESTADO, "PENDIENTE")
                put(C_BOX_INTENTOS, 0)
                put(C_BOX_CREADO_EN, now)
            }
            db.insertOrThrow(TABLE_OUTBOX, null, outbox)
            db.setTransactionSuccessful()
            return "$quantity $unit de $productName"
        } finally {
            db.endTransaction()
        }
    }

    fun getAllMovimientos(): List<OfflineTransaction> {
        val rows = mutableListOf<OfflineTransaction>()
        readableDatabase.rawQuery(
            """
            SELECT m.$C_MOV_TX_LOCAL, p.$C_PRODUCTO_DESC, p.$C_PRODUCTO_UNIDAD,
                   m.$C_MOV_CANTIDAD, m.$C_MOV_DIIO, m.$C_MOV_FECHA_CLIENTE,
                   m.$C_MOV_ESTADO_SYNC
            FROM $TABLE_MOVIMIENTO m
            INNER JOIN $TABLE_PRODUCTO p
              ON p.$C_PRODUCTO_ID = m.$C_MOV_ID_PRODUCTO
            ORDER BY m.rowid DESC
            """.trimIndent(), null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val millis = cursor.getString(5).toLongOrNull()
                val displayDate = millis?.let { DateFormat.getDateTimeInstance().format(Date(it)) }
                    ?: "Fecha no disponible"
                rows += OfflineTransaction(
                    id = cursor.getString(0),
                    title = cursor.getDouble(3).toString() + " " + cursor.getString(2) + " · " + cursor.getString(1),
                    timeLocation = displayDate,
                    diio = if (cursor.isNull(4)) null else "DIIO: " + cursor.getString(4),
                    isSynced = cursor.getString(6) == "PROCESADO"
                )
            }
        }
        return rows
    }

    fun getPendingSyncEvents(): List<PendingSyncEvent> {
        val events = mutableListOf<PendingSyncEvent>()
        readableDatabase.query(
            TABLE_OUTBOX, arrayOf(C_BOX_ID_EVENTO, C_BOX_TX_LOCAL, C_BOX_DOC_JSON),
            "$C_BOX_ESTADO IN ('PENDIENTE', 'ERROR')",
            null, null, null, "$C_BOX_CREADO_EN ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                events += PendingSyncEvent(cursor.getString(0), cursor.getString(1), cursor.getString(2))
            }
        }
        return events
    }

    fun markEventSynced(transactionId: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.update(TABLE_MOVIMIENTO,
                ContentValues().apply { put(C_MOV_ESTADO_SYNC, "PROCESADO") },
                "$C_MOV_TX_LOCAL = ?", arrayOf(transactionId))
            db.update(TABLE_OUTBOX,
                ContentValues().apply {
                    put(C_BOX_ESTADO, "ENVIADO")
                    put(C_BOX_PROCESADO_EN, System.currentTimeMillis().toString())
                },
                "$C_BOX_TX_LOCAL = ?", arrayOf(transactionId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun markEventAttemptFailed(transactionId: String) {
        writableDatabase.execSQL(
            """
            UPDATE $TABLE_OUTBOX
            SET $C_BOX_INTENTOS = $C_BOX_INTENTOS + 1, $C_BOX_ESTADO = 'ERROR'
            WHERE $C_BOX_TX_LOCAL = ?
            """.trimIndent(), arrayOf(transactionId)
        )
    }
}
