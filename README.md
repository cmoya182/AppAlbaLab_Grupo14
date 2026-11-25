# Alba Lab - Red Sociolaboral CORPALIV

## 1. Nombre del Proyecto
**Alba Lab - CORPALIV** (Aplicación Móvil de Vinculación Sociolaboral, Tienda Solidaria, Indicadores Económicos, Noticias Oficiales y Gestión de Solicitudes).

## 2. Integrantes
- **Cristóbal Gabriel Moya Flores**
- **Virgilio Exequiel Herrera Jara**

## 3. Funcionalidades Principales
- **Inicio Adaptable (Responsive) con UF Diaria en Tiempo Real:** Diseño compatible con pantallas compactas, medianas y expandidas (`WindowSizeClass`), con banner actualizado diariamente con el valor de la UF chilena (`mindicador.cl`) como referencia para convenios RSE y donaciones.
- **Navegación Fluida y Resiliente:** Barra inferior flotante persistente con animaciones direccionales suaves y preservación de estado ante rotaciones de pantalla mediante `rememberSaveable`.
- **Tienda Solidaria:** Cuadrícula de productos artesanales confeccionados en los talleres laborales de CORPALIV, con búsqueda validada, filtro por categoría y detalle en hoja modal.
- **Formularios con Recursos Nativos y Persistencia:** Postulación a Voluntariado, Convenio Empresa/RSE y Compra Solidaria con integración nativa de Cámara y Galería y almacenamiento local en Room SQLite.
- **Noticias Oficiales CORPALIV (API Externa):** Consumo REST en vivo desde `corpaliv.cl` disponible para usuarios invitados.
- **Panel Institucional (CRUD Conectado):** Gestión administrativa protegida por rol conectada al microservicio propio en Spring Boot (`GET`, `POST`, `PUT`, `DELETE`).
- **Simulador de Impacto Social:** Cálculo referencial de kits de insumos y sesiones financiadas según el aporte simulado.

## 4. Endpoints Utilizados
### A) APIs Externas (Consumo vía Retrofit)
- `GET https://mindicador.cl/api/uf` -> Obtiene el valor diario actualizado de la Unidad de Fomento (UF) en Chile para el banner del Inicio.
- `GET https://corpaliv.cl/wp-json/wp/v2/posts?per_page=10&_embed=true` -> Obtiene las publicaciones oficiales y actualizadas de la corporación CORPALIV.

### B) Microservicio Propio en Spring Boot (`alba-lab-api`)
- `GET /api/solicitudes` -> Lista todas las solicitudes registradas en la base de datos.
- `POST /api/solicitudes` -> Crea y guarda una nueva solicitud enviada desde la app móvil.
- `PUT /api/solicitudes/{id}` -> Actualiza una solicitud o cambia su estado (`PENDIENTE` / `ATENDIDA`).
- `DELETE /api/solicitudes/{id}` -> Elimina una solicitud específica por su identificador.

## 5. Pasos para Ejecutar
1. Abrir el proyecto Spring Boot `alba-lab-api` y ejecutar `AlbalabApiApplication.java` (inicia en `http://localhost:8080`).
2. Abrir el proyecto Android `AppAlbaLab_Grupo14` en Android Studio y presionar **Sync Now** para sincronizar Gradle.
3. Ejecutar en un emulador Android (API 24 o superior) o dispositivo físico con conexión a internet.
4. Para ingresar al Panel Institucional como administrador, usar las credenciales de demostración: `admin@corpaliv.cl` / `albalab2026`.

## 6. Captura del APK Firmado y Keystore (.jks)
*(Cristóbal agregará aquí la captura final de `app-release.apk` y `albalab-key.jks` al completar el empaquetado final)*