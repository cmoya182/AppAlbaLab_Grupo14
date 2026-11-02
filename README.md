# Aplicación Móvil Alba Lab - CORPALIV

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** - Duoc UC.

## Integrantes del Equipo (Grupo 14)
* **Cristóbal Gabriel Moya Flores**
* **Virgilio Exequiel Herrera Jara**

---

## 1. Descripción del Caso y Propósito
**Alba Lab** es la iniciativa de inclusión sociolaboral de la corporación **CORPALIV**, enfocada en jóvenes y adultos con discapacidad múltiple y sordoceguera. Esta aplicación móvil permite visibilizar el catálogo de productos elaborados en los talleres laborales, simular el impacto social de aportes financieros y canalizar solicitudes formales de vinculación corporativa, alianzas, voluntariado o compras solidarias.

---

## 2. Funcionalidades Implementadas
* **Autenticación Institucional con Persistencia de Sesión:** Inicio de sesión para colaboradores (`admin@corpaliv.cl` / `albalab2026`) respaldado por `SharedPreferences` para recordar la sesión activa entre reinicios de la aplicación.
* **Catálogo Interactivo con Filtros y Búsqueda Validada:** Visualización de productos solidarios clasificados por taller (*Aromas*, *Textil*, *Cerámica*) mediante `FilterChip`, barra de búsqueda con validación de caracteres en tiempo real y tarjetas expandibles con animación `animateContentSize`.
* **Formularios Segmentados y Validados:** Tres flujos independientes (*Convenio Empresa / RSE*, *Cotización Compra Solidaria* con producto fijo bloqueado y *Postulación Voluntariado*). Validación centralizada y desacoplada en `MainViewModel` por cada campo (nombre/empresa, correo con Regex, teléfono numérico y detalle de propuesta) con retroalimentación visual (`isError`, `supportingText` e íconos de estado).
* **Persistencia Local Relacional con Room (SQLite):** Almacenamiento permanente de las solicitudes enviadas en la tabla `solicitudes_vinculacion`. Incluye lectura reactiva en tiempo real mediante `Flow` y `StateFlow` en el Panel Institucional, filtrado por tipo, borrado individual por `id` y vaciado general del historial.
* **Acceso a Recursos Nativos (Cámara, Galería e Intent de Compartición):** Captura de fotografías en tiempo real mediante `TakePicturePreview` con solicitud segura de permisos en tiempo de ejecución (`RequestPermission`), selección de imágenes desde el almacenamiento local decodificadas con `BitmapFactory` y compartición nativa de solicitudes mediante `Intent.ACTION_SEND`.
* **Simulador de Impacto Social:** Herramienta interactiva validada en la sección de Configuración que calcula la equivalencia de un aporte en kits de insumos y sesiones de apoyo sociolaboral, desplegando el resultado mediante `AnimatedVisibility`.
* **Diseño Adaptable (Responsive) y Microinteracciones:** Soporte automático para pantallas compactas (`WindowWidthSizeClass.Compact`), medianas y expandidas, junto con animaciones de entrada escalonada en `graphicsLayer` y respuesta táctil con física de resorte (`spring`).

---

## 3. Arquitectura del Proyecto (MVVM)
El código está estructurado siguiendo principios de modularidad y separación de responsabilidades:
* `data/local/`: Contiene `SolicitudEntity.kt`, `SolicitudDao.kt` y `AppDatabase.kt` (Patrón Singleton para Room SQLite).
* `model/`: Clases de datos del dominio (`Producto.kt`).
* `navigation/`: Definición de rutas seguras mediante `sealed class Screen` y eventos en `NavigationEvent.kt`.
* `ui/components/` y `ui/screens/`: Componentes reutilizables y vistas declarativas en Jetpack Compose (`LoginScreen`, `HomeScreen`, `HomeScreenCompacta`, `HomeScreenMediana`, `HomeScreenExpandida`, `CatalogoScreen`, `ContactoScreen`, `ProfileScreen`, `SettingsScreen`).
* `ui/theme/`: Configuración visual Material Design 3 con la paleta institucional de CORPALIV (`Color.kt`, `Theme.kt`, `Type.kt`).
* `viewmodels/`: `MainViewModel.kt` que concentra el estado reactivo de la UI, validaciones y operaciones asíncronas en segundo plano (`Dispatchers.IO`).

---

## 4. Planificación y Gestión Colaborativa
* **Tablero de Planificación en Trello:** https://trello.com/b/kVOrf9cf/proyecto-apps-moviles
* **Repositorio GitHub:** https://github.com/cmoya182/AppAlbaLab_Grupo14

---

## 5. Instrucciones para Ejecutar el Proyecto
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/cmoya182/AppAlbaLab_Grupo14.git
   ```
2. Abrir la carpeta raíz del proyecto en **Android Studio**.
3. Esperar la sincronización automática de dependencias con **Gradle** y **KSP**.
4. Ejecutar el módulo `app` (`Shift + F10`) en un emulador o dispositivo físico con **Android 7.0 (API 24)** o superior.
