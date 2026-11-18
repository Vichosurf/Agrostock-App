# AgroStock App

Aplicación Android nativa para inventario agrícola y trazabilidad de retiros en Agrícola Moncopulli S.A. Desarrollada con Kotlin, Jetpack Compose Material 3 y SQLite nativo.

## Funcionalidades

- Acceso local con RUT y PIN de seis dígitos.
- Validación de RUT por Módulo 11.
- Verificación de PIN con PBKDF2-HMAC-SHA256 y sal individual.
- Catálogo cargado desde SQLite, búsqueda y filtros por categoría.
- Retiros con cantidad validada contra stock disponible.
- DIIO obligatorio según la categoría y validado contra animales activos del registro local.
- Registro transaccional de movimiento, descuento de stock y evento pendiente en sync_outbox.
- Detección de conectividad validada y cola offline.
- Cliente HTTP para POST /api/movimientos/sync; un evento solo se confirma con respuesta aceptada que devuelva el mismo id_transaccion_local.

## Limitaciones conocidas

- SQLite estándar no cifra el archivo completo de la base de datos. Antes de usar datos reales debe incorporarse cifrado en reposo y gestión de claves.
- La cámara/lectura QR no está integrada; el DIIO se ingresa manualmente.
- La validación DIIO utiliza el maestro local, no una consulta directa al SAG.
- La sincronización necesita un backend accesible y registros maestros de usuario, producto y animal cuyos identificadores coincidan con los que envía el dispositivo.
- La URL del backend no está incluida en el repositorio ni contiene credenciales.

## Configuración del entorno

1. Instalar Android Studio, Android SDK Platform 36 y JDK 25 para Gradle.
2. Clonar el repositorio y abrirlo en Android Studio.
3. Ejecutar Sync Project with Gradle Files.
4. Para compilar el APK debug:

   ```bash
   ./gradlew :app:assembleDebug
   ```

5. Para configurar el backend, entregar la URL base durante la compilación:

   ```bash
   ./gradlew :app:assembleDebug -PAGROSTOCK_API_BASE_URL=https://tu-servidor
   ```

   Para desarrollo local, HTTP se permite únicamente en 10.0.2.2, localhost o 127.0.0.1. Los entornos reales deben utilizar HTTPS.

## Pruebas

Pruebas unitarias:

```bash
./gradlew :app:testDebugUnitTest
```

Pruebas instrumentadas con dispositivo/emulador conectado:

```bash
./gradlew :app:connectedDebugAndroidTest
```

La suite agregada cubre RUT válido/inválido, autenticación con PIN derivado, estructura de diez tablas, claves foráneas, stock, cantidad e identificación DIIO.

## Arquitectura de paquetes

- ui/: pantallas Compose.
- data/: modelos y Repository.
- data/local/: SQLiteOpenHelper, transacciones y outbox.
- data/remote/: cliente HTTP FastAPI.
- util/: validación RUT, PBKDF2 y conectividad.

El contrato de sincronización usa id_transaccion_local como clave idempotente. El cliente marca un movimiento como procesado solo después de recibir HTTP 2xx con el mismo identificador y estado PROCESADO o YA_PROCESADO.
