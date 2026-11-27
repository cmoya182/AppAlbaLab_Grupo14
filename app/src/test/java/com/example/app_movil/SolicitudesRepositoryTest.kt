package com.example.app_movil

import com.example.app_movil.data.local.SolicitudDao
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.data.remote.SolicitudApiService
import com.example.app_movil.data.remote.SolicitudDto
import com.example.app_movil.data.repository.SolicitudesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

// Pruebas unitarias CRUD de SolicitudesRepository usando MockK sobre SolicitudDao y SolicitudApiService
class SolicitudesRepositoryTest {

    private lateinit var daoMock: SolicitudDao
    private lateinit var apiMock: SolicitudApiService
    private lateinit var repository: SolicitudesRepository

    @Before
    fun setUp() {
        daoMock = mockk(relaxed = true)
        apiMock = mockk()
        repository = SolicitudesRepository(solicitudDao = daoMock, apiService = apiMock)
    }

    // GET /api/solicitudes: inserta nuevas, actualiza existentes conservando imagen local y borra las eliminadas en servidor
    @Test
    fun sincronizarConServidor_getExitoso_sincronizaInsertUpdateYDeleteEnRoom() = runTest {
        val locales = listOf(
            SolicitudEntity(
                id = 1,
                tipoContacto = "Empresa",
                nombre = "Empresa Antigua",
                correo = "old@empresa.cl",
                telefono = "+56911111111",
                mensaje = "Mensaje inicial",
                fecha = "20/11/2026 10:00",
                imagenPath = "/ruta/local/foto.jpg",
                estado = "PENDIENTE",
                remoteId = 10L
            ),
            SolicitudEntity(
                id = 2,
                tipoContacto = "Voluntariado",
                nombre = "Registro Borrado En Servidor",
                correo = "borrado@correo.cl",
                telefono = "+56922222222",
                mensaje = "Ya no existe en servidor",
                fecha = "19/11/2026 09:00",
                estado = "PENDIENTE",
                remoteId = 99L
            )
        )
        val remotas = listOf(
            SolicitudDto(
                id = 10L,
                tipoContacto = "Empresa",
                nombre = "Empresa Actualizada",
                correo = "nuevo@empresa.cl",
                telefono = "+56911111111",
                mensaje = "Mensaje actualizado desde API",
                fecha = "20/11/2026 10:00",
                estado = "ATENDIDA"
            ),
            SolicitudDto(
                id = 20L,
                tipoContacto = "Compra Solidaria",
                nombre = "Comprador Nuevo",
                correo = "compra@correo.cl",
                telefono = "+56933333333",
                mensaje = "Cotización de 10 tazones",
                fecha = "24/11/2026 12:00",
                estado = "PENDIENTE"
            )
        )

        coEvery { apiMock.listarSolicitudes() } returns remotas
        every { daoMock.obtenerTodasSincrono() } returns locales

        val resultado = repository.sincronizarConServidor()

        assertTrue(resultado.isSuccess)
        assertEquals(2, resultado.getOrNull())
        // Se elimina de Room el registro cuya remoteId (99L) ya no viene desde el servidor
        verify(exactly = 1) { daoMock.eliminarSolicitudPorId(2) }
        // Se actualiza el registro existente (remoteId 10L) preservando su imagenPath local
        verify(exactly = 1) {
            daoMock.actualizarSolicitud(
                match {
                    it.id == 1 &&
                        it.remoteId == 10L &&
                        it.nombre == "Empresa Actualizada" &&
                        it.estado == "ATENDIDA" &&
                        it.imagenPath == "/ruta/local/foto.jpg"
                }
            )
        }
        // Se inserta el nuevo registro remoto (remoteId 20L)
        verify(exactly = 1) {
            daoMock.insertarSolicitud(
                match { it.remoteId == 20L && it.nombre == "Comprador Nuevo" }
            )
        }
    }

    // Sincronización con pendientes locales (remoteId == null): sube primero vía POST, actualiza Room y luego ejecuta GET
    @Test
    fun sincronizarConServidor_conPendientesLocales_subePorPostYActualizaRemoteIdAntesDeGet() = runTest {
        val pendienteLocal = SolicitudEntity(
            id = 5,
            tipoContacto = "Voluntariado",
            nombre = "Sofía Méndez",
            correo = "sofia@correo.cl",
            telefono = "+56977778888",
            mensaje = "Creada mientras el servidor estaba desconectado.",
            fecha = "26/11/2026 20:00",
            imagenPath = "/ruta/local/sofia.jpg",
            estado = "PENDIENTE",
            remoteId = null
        )
        val creadaRemota = SolicitudDto(
            id = 77L,
            tipoContacto = "Voluntariado",
            nombre = "Sofía Méndez",
            correo = "sofia@correo.cl",
            telefono = "+56977778888",
            mensaje = "Creada mientras el servidor estaba desconectado.",
            fecha = "26/11/2026 20:00",
            estado = "PENDIENTE"
        )
        val localTrasPost = pendienteLocal.copy(remoteId = 77L)

        every { daoMock.obtenerTodasSincrono() } returnsMany listOf(
            listOf(pendienteLocal),
            listOf(localTrasPost)
        )
        coEvery { apiMock.crearSolicitud(any()) } returns Response.success(creadaRemota)
        coEvery { apiMock.listarSolicitudes() } returns listOf(creadaRemota)

        val resultado = repository.sincronizarConServidor()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull())
        // Se sube el pendiente mediante POST /api/solicitudes
        coVerify(exactly = 1) { apiMock.crearSolicitud(match { it.id == null && it.nombre == "Sofía Méndez" }) }
        // Luego se ejecuta GET /api/solicitudes
        coVerify(exactly = 1) { apiMock.listarSolicitudes() }
        // Se actualiza el registro local con el remoteId asignado (77L) conservando su imagen local
        verify(atLeast = 1) {
            daoMock.actualizarSolicitud(
                match { it.id == 5 && it.remoteId == 77L && it.imagenPath == "/ruta/local/sofia.jpg" }
            )
        }
        // No debe duplicar el registro como una inserción nueva
        verify(exactly = 0) { daoMock.insertarSolicitud(any()) }
    }

    // GET /api/solicitudes: ante fallo de red retorna Result.failure sin romper la app
    @Test
    fun sincronizarConServidor_conErrorDeRed_retornaFailure() = runTest {
        coEvery { apiMock.listarSolicitudes() } throws IOException("Connection refused")

        val resultado = repository.sincronizarConServidor()

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull() is IOException)
    }

    // POST /api/solicitudes: guarda remoteId asignado por el servidor y persiste en Room
    @Test
    fun crearSolicitud_postExitoso_guardaConRemoteIdEnRoom() = runTest {
        val entidadNueva = SolicitudEntity(
            tipoContacto = "Voluntariado",
            nombre = "Ana Torres",
            correo = "ana@corpaliv.cl",
            telefono = "+56944445555",
            mensaje = "Quiero participar los sábados en el taller.",
            fecha = "26/11/2026 15:00"
        )
        val respuestaServidor = SolicitudDto(
            id = 55L,
            tipoContacto = "Voluntariado",
            nombre = "Ana Torres",
            correo = "ana@corpaliv.cl",
            telefono = "+56944445555",
            mensaje = "Quiero participar los sábados en el taller.",
            fecha = "26/11/2026 15:00",
            estado = "PENDIENTE"
        )

        coEvery { apiMock.crearSolicitud(any()) } returns Response.success(respuestaServidor)
        every { daoMock.insertarSolicitud(any()) } returns 7L

        val resultado = repository.crearSolicitud(entidadNueva)

        assertTrue(resultado.isSuccess)
        assertEquals(7, resultado.getOrNull()?.id)
        assertEquals(55L, resultado.getOrNull()?.remoteId)
        coVerify(exactly = 1) { apiMock.crearSolicitud(any()) }
        verify(exactly = 1) { daoMock.insertarSolicitud(match { it.remoteId == 55L }) }
    }

    // POST /api/solicitudes: si el servidor no responde, respalda la solicitud localmente en Room
    @Test
    fun crearSolicitud_sinConexion_respaldaLocalmenteYRetornaFailure() = runTest {
        val entidadNueva = SolicitudEntity(
            tipoContacto = "Empresa",
            nombre = "Constructora Sur",
            correo = "rse@sur.cl",
            telefono = "+56988887777",
            mensaje = "Propuesta de convenio RSE anual.",
            fecha = "26/11/2026 16:00"
        )

        coEvery { apiMock.crearSolicitud(any()) } throws IOException("Servidor apagado")

        val resultado = repository.crearSolicitud(entidadNueva)

        assertTrue(resultado.isFailure)
        verify(exactly = 1) { daoMock.insertarSolicitud(entidadNueva) }
    }

    // PUT /api/solicitudes/{id}: actualiza mensaje y estado en servidor y Room cuando existe remoteId
    @Test
    fun actualizarSolicitud_putExitoso_actualizaEnApiYRoom() = runTest {
        val entidadEditada = SolicitudEntity(
            id = 3,
            tipoContacto = "Empresa",
            nombre = "Empresa Demo",
            correo = "contacto@demo.cl",
            telefono = "+56966667777",
            mensaje = "Seguimiento realizado",
            fecha = "26/11/2026 17:00",
            estado = "ATENDIDA",
            remoteId = 15L
        )
        val respuestaPut = SolicitudDto(
            id = 15L,
            tipoContacto = "Empresa",
            nombre = "Empresa Demo",
            correo = "contacto@demo.cl",
            telefono = "+56966667777",
            mensaje = "Seguimiento realizado",
            fecha = "26/11/2026 17:00",
            estado = "ATENDIDA"
        )

        coEvery { apiMock.actualizarSolicitud(15L, any()) } returns Response.success(respuestaPut)

        val resultado = repository.actualizarSolicitud(entidadEditada)

        assertTrue(resultado.isSuccess)
        assertEquals("ATENDIDA", resultado.getOrNull()?.estado)
        coVerify(exactly = 1) { apiMock.actualizarSolicitud(15L, any()) }
        verify(exactly = 1) {
            daoMock.actualizarSolicitud(match { it.remoteId == 15L && it.estado == "ATENDIDA" })
        }
    }

    // PUT /api/solicitudes/{id}: si falla con HTTP 500, guarda cambio en local y devuelve Failure
    @Test
    fun actualizarSolicitud_conErrorHttp_actualizaLocalYRetornaFailure() = runTest {
        val entidad = SolicitudEntity(
            id = 4,
            tipoContacto = "Compra Solidaria",
            nombre = "Luis Soto",
            correo = "luis@correo.cl",
            telefono = "+56955556666",
            mensaje = "Detalle actualizado",
            fecha = "26/11/2026 17:30",
            estado = "ATENDIDA",
            remoteId = 18L
        )

        coEvery { apiMock.actualizarSolicitud(18L, any()) } returns
            Response.error(500, ResponseBody.create(null, "Error interno"))

        val resultado = repository.actualizarSolicitud(entidad)

        assertTrue(resultado.isFailure)
        verify(exactly = 1) { daoMock.actualizarSolicitud(entidad) }
    }

    // DELETE /api/solicitudes/{id}: elimina en el servidor y en Room devolviendo la ruta de imagen local
    @Test
    fun eliminarSolicitud_deleteExitoso_eliminaEnServidorYRoom() = runTest {
        val existente = SolicitudEntity(
            id = 9,
            tipoContacto = "Voluntariado",
            nombre = "Camila Vega",
            correo = "camila@correo.cl",
            telefono = "+56912345678",
            mensaje = "Postulación voluntariado",
            fecha = "26/11/2026 18:00",
            imagenPath = "/files/adjunto_9.jpg",
            remoteId = 33L
        )

        every { daoMock.obtenerSolicitudPorId(9) } returns existente
        coEvery { apiMock.eliminarSolicitud(33L) } returns Response.success(Unit)

        val resultado = repository.eliminarSolicitud(9)

        assertTrue(resultado.isSuccess)
        assertEquals("/files/adjunto_9.jpg", resultado.getOrNull())
        coVerify(exactly = 1) { apiMock.eliminarSolicitud(33L) }
        verify(exactly = 1) { daoMock.eliminarSolicitudPorId(9) }
    }

    // DELETE /api/solicitudes/{id}: si falla la red, borra localmente y reporta el error remoto
    @Test
    fun eliminarSolicitud_conErrorDeRed_eliminaEnRoomYRetornaFailure() = runTest {
        val existente = SolicitudEntity(
            id = 12,
            tipoContacto = "Empresa",
            nombre = "Comercial Norte",
            correo = "info@norte.cl",
            telefono = "+56999990000",
            mensaje = "Consulta alianza",
            fecha = "26/11/2026 18:30",
            remoteId = 44L
        )

        every { daoMock.obtenerSolicitudPorId(12) } returns existente
        coEvery { apiMock.eliminarSolicitud(44L) } throws IOException("Timeout al eliminar")

        val resultado = repository.eliminarSolicitud(12)

        assertTrue(resultado.isFailure)
        verify(exactly = 1) { daoMock.eliminarSolicitudPorId(12) }
    }
}
