# Alba Lab - Red Sociolaboral CORPALIV

## 1. Nombre del Proyecto
**Alba Lab - CORPALIV** (Aplicación Móvil de Vinculación Sociolaboral, Tienda Solidaria, Noticias Oficiales y Gestión de Solicitudes).

## 2. Integrantes
- **Cristóbal Gabriel Moya Flores**
- **Virgilio Exequiel Herrera Jara**

## 3. Funcionalidades Principales
- **Inicio Adaptable (Responsive):** Diseño compatible con pantallas compactas, medianas y expandidas (`WindowSizeClass`) con acceso a Tienda Solidaria, Convenio Empresa/RSE y Voluntariado.
- **Tienda Solidaria:** Cuadrícula de productos confeccionados en talleres laborales de CORPALIV con búsqueda validada, filtros por categoría y detalle en hoja modal.
- **Formularios con Recursos Nativos:** Registro de solicitudes con integración nativa de Cámara y Galería y persistencia local en Room SQLite.
- **Noticias Oficiales CORPALIV (API Externa):** Consumo REST en tiempo real desde `corpaliv.cl` exclusivo para la experiencia de invitados.
- **Panel Institucional (CRUD Conectado):** Gestión administrativa protegida por rol conectada al microservicio Spring Boot (`GET`, `POST`, `PUT`, `DELETE`).
- **Simulador de Impacto Social:** Cálculo referencial de kits de insumos y sesiones financiadas según el monto ingresado.

## 4. Endpoints Utilizados
### A) API Externa (Noticias Oficiales CORPALIV)
- `GET https://corpaliv.cl/wp-json/wp/v2/posts?per_page=10&_embed=true` -> Obtiene las publicaciones reales de CORPALIV.

### B) Microservicio Propio en Spring Boot (`alba-lab-api`)
- `GET /api/solicitudes` -> Lista todas las solicitudes registradas.
- `POST /api/solicitudes` -> Crea una nueva solicitud enviada desde la app móvil.
- `PUT /api/solicitudes/{id}` -> Actualiza una solicitud o cambia su estado (`PENDIENTE` / `ATENDIDA`).
- `DELETE /api/solicitudes/{id}` -> Elimina una solicitud específica por su ID.

## 5. Pasos para Ejecutar
1. Abrir el proyecto Spring Boot `alba-lab-api` y ejecutar `AlbalabApiApplication.java` (puerto `8080`).
2. Abrir el proyecto Android `AppAlbaLab_Grupo14` en Android Studio y sincronizar Gradle (`Sync Now`).
3. Ejecutar en un emulador Android (API 24 o superior) con conexión a internet.
4. Para probar el Panel Institucional, iniciar sesión con `admin@corpaliv.cl` / `albalab2026`.

## 6. Captura del APK Firmado y Keystore (.jks)
*(Cristóbal agregará aquí la captura final de `app-release.apk` y `albalab-key.jks` al cerrar el empaquetado)*
