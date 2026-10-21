# Aplicación Móvil Alba Lab - CORPALIV

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**.

## Integrantes (Grupo 14)
* **Cristobal Moya**
* **Virgilio Herrera**

## Propósito del Proyecto
Solución móvil orientada a visibilizar y articular la oferta sociolaboral de los talleres de **CORPALIV (Alba Lab)**, permitiendo a empresas y donantes conocer los productos solidarios, canalizar solicitudes formales de vinculación y apoyar la inclusión de personas con sordoceguera y discapacidad múltiple.

## Arquitectura y Tecnologías
* **Patrón de Arquitectura:** MVVM (Model - View - ViewModel) con separación estricta de lógica y presentación.
* **UI Toolkit:** Jetpack Compose con paleta de diseño institucional CORPALIV.
* **Navegación:** `NavHost` controlado mediante una clase sellada (`sealed class Screen`).
* **Persistencia Local:** Base de datos relacional interna mediante **Room (SQLite)** gestionada con corrutinas en `Dispatchers.IO`.
* **Recursos Nativos:** Acceso a **Cámara** y **Galería** mediante la API de `rememberLauncherForActivityResult` con validación de permisos en tiempo de ejecución.
* **Animaciones:** Transiciones fluidas con `AnimatedVisibility` y tarjetas expandibles con `animateContentSize`.
* **Validación de Formularios:** Reglas de validación centralizadas en `MainViewModel` con retroalimentación visual (`isError`, `supportingText`).

## Requisitos de Ejecución
1. Android Studio Ladybug o superior.
2. JDK 11 o superior.
3. Dispositivo físico o Emulador Android con API 24 o superior.

## Planificación
La distribución y seguimiento del proyecto se encuentra disponible en el tablero de [Trello](ENLACE_A_TU_TRELLO_AQUI).