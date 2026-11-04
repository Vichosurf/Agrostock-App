# AgroStock App 🌾🐄

Aplicación móvil agrícola y ganadera desarrollada en **Android Native con Jetpack Compose** para el control de inventario, registro de retiros de insumos en terreno, verificación de arete animal (DIIO / SAG Chile) y gestión de cola de sincronización offline con SQLite.

---

## 📸 Pantallas y Flujo de Trabajo

### 1. Acceso Rápido (PIN de 6 dígitos)
- Autenticación segura local offline.
- Teclado numérico interactivo 3x4 con indicador de 6 dígitos.
- Persistencia local con SQLite cifrado.

### 2. Registro de Retiro y DIIO (Módulo Sanitario)
- Detalle de insumo con distintivo de **Fármaco Controlado**.
- Ajuste rápido de dosis/unidades (`-10`, `-5`, `-1`, `+1`, `+5`, `+10`).
- Módulo de arete animal obligatorio DIIO con verificación SAG Chile y escáner QR.

### 3. Catálogo y Búsqueda (Modo Online)
- Búsqueda e insumos en tiempo real.
- Filtro por categorías: **Forrajes, Sales, Fármacos y Fertilizantes**.
- Tarjetas de producto con control de stock y acciones rápidas.

### 4. Cola de Sincronización Offline
- Retención local de transacciones retenidas en SQLite (`SYNC_PENDING`).
- Forzado de sincronización condicionado por estado de red Wi-Fi.
- Métricas en tiempo real de uso de almacenamiento y batería.

---

## 🛠️ Tecnologías Utilizadas

- **Kotlin** & **Jetpack Compose (Material 3)**
- **Compose Material Icons Extended**
- **Architecture**: Single Activity with Modular Screen Composables & State Management
- **Custom Graphics**: Canvas Vector Graphics para el Logo oficial de AgroStock (Árbol, Manzana y Vaca)

---

## 🚀 Instalación y Ejecución

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Vichosurf/Agrostock-App.git
   ```
2. Abrir en **Android Studio**.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Compilar y ejecutar en un dispositivo o emulador Android.
