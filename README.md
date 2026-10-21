# Aplicación Móvil Alba Lab - CORPALIV

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** - Duoc UC.

## Integrantes del Equipo (Grupo 14)
* **Cristóbal Gabriel Moya Flores**
* **Virgilio Exequiel Herrera Jara**

---

## 1. Descripción del Caso y Propósito
**Alba Lab** es la iniciativa de inclusión sociolaboral de la corporación **CORPALIV**, enfocada en jóvenes y adultos con discapacidad múltiple y sordoceguera. Esta aplicación móvil permite visibilizar el catálogo de productos elaborados en los talleres laborales, simular el impacto social de aportes financieros y canalizar solicitudes formales de vinculación corporativa, alianzas o compras solidarias.

---

## 2. Funcionalidades Implementadas
* **Catálogo Interactivo con Filtros y Búsqueda Validada:** Visualización de productos solidarios clasificados por taller (*Aromas*, *Textil*, *Cerámica*) mediante `FilterChip`, barra de búsqueda con validación de caracteres en tiempo real y tarjetas expandibles con animación `animateContentSize`.
* **Formulario de Vinculación Institucional:** Validación desacoplada en `MainViewModel` por cada campo (nombre/empresa, formato de correo electrónico con Regex, longitud numérica de teléfono y detalle de propuesta) con retroalimentación visual (`isError`, `supportingText` e íconos de estado).
* **Persistencia Local con Room (SQLite):** Almacenamiento permanente de las solicitudes enviadas en la tabla `solicitudes_vinculacion`. Incluye lectura reactiva mediante `Flow`/`StateFlow` en la vista de Perfil, borrado individual por registro (`id`) y vaciado general de la base de datos.
* **Acceso a Recursos Nativos (Cámara y Galería):** Captura de fotografías en tiempo real mediante `TakePicturePreview` con solicitud segura de permisos en tiempo de ejecución (`RequestPermission`) y selección de imágenes desde el almacenamiento local decodificadas con `BitmapFactory`.
* **Simulador de Impacto Social:** Herramienta interactiva validada en la sección de Configuración que calcula la equivalencia de un aporte en kits de insumos y sesiones de taller, desplegando el resultado mediante `AnimatedVisibility`.
* **Diseño Adaptable (Responsive):** Soporte automático para pantallas compactas (`WindowWidthSizeClass.Compact`) y pantallas extendidas/tablets.

---

## 3. Arquitectura del Proyecto (MVVM)
El código está estructurado siguiendo principios de modularidad y separación de responsabilidades:
* `data/local/`: Contiene `SolicitudEntity.kt`, `SolicitudDao.kt` y `AppDatabase.kt` (Patrón Singleton para Room).
* `model/`: Clases de datos del dominio (`Producto.kt`).
* `navigation/`: Definición de rutas seguras mediante `sealed class Screen` y eventos en `NavigationEvent.kt`.
* `ui/components/` y `ui/screens/`: Componentes reutilizables y vistas declarativas en Jetpack Compose (`HomeScreen`, `CatalogoScreen`, `ContactoScreen`, `ProfileScreen`, `SettingsScreen`).
* `viewmodels/`: `MainViewModel.kt` que concentra el estado de la UI y las operaciones en segundo plano (`Dispatchers.IO`).

---

## 4. Planificación y Gestión Colaborativa
* **Tablero de Planificación en Trello:** [https://trello.com/b/kVOrf9cf/proyecto-apps-moviles]

---

## 5. Instrucciones para Ejecutar el Proyecto
1. Clonar el repositorio:
   ```bash
   git clone [https://github.com/cmoya182/AppAlbaLab_Grupo14.git](https://github.com/cmoya182/AppAlbaLab_Grupo14.git)