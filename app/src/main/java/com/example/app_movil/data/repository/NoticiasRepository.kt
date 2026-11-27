package com.example.app_movil.data.repository

import androidx.core.text.HtmlCompat
import com.example.app_movil.data.remote.CorpalivApiService
import com.example.app_movil.data.remote.RetrofitClient
import com.example.app_movil.data.remote.WordPressPostDto
import com.example.app_movil.model.Noticia
import java.text.SimpleDateFormat
import java.util.Locale

class NoticiasRepository(
    private val apiService: CorpalivApiService = RetrofitClient.apiService
) {
    companion object {
        // Regex precompilada para evitar instanciación repetida al limpiar HTML
        private val REGEX_HTML_TAGS = Regex("<[^>]*>")

        // Elimina etiquetas HTML y entidades especiales con ruta rápida para texto plano
        fun limpiarHtml(textoHtml: String): String {
            if (!textoHtml.contains('<') && !textoHtml.contains('&')) {
                return textoHtml.replace("\uFFFC", "").trim()
            }
            val texto = try {
                HtmlCompat.fromHtml(textoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            } catch (_: RuntimeException) {
                // Respaldo si Android no está disponible (pruebas unitarias): quita etiquetas HTML
                textoHtml.replace(REGEX_HTML_TAGS, "")
            }
            return texto.replace("\uFFFC", "").trim()
        }

        // Transforma fecha ISO-8601 a dd/MM/yyyy sin instanciar SimpleDateFormat en el caso estándar
        fun formatearFechaIso(fechaIso: String): String {
            if (fechaIso.length >= 10 && fechaIso[4] == '-' && fechaIso[7] == '-') {
                val anio = fechaIso.substring(0, 4)
                val mes = fechaIso.substring(5, 7)
                val dia = fechaIso.substring(8, 10)
                if (anio.all(Char::isDigit) && mes.all(Char::isDigit) && dia.all(Char::isDigit)) {
                    return "$dia/$mes/$anio"
                }
            }
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = parser.parse(fechaIso)
                if (date != null) formatter.format(date) else fechaIso.take(10)
            } catch (_: Exception) {
                fechaIso.take(10)
            }
        }

        // Convierte un objeto DTO remoto al modelo limpio Noticia usado por la interfaz
        fun mapearDtoANoticia(dto: WordPressPostDto): Noticia {
            val resumenLimpio = limpiarHtml(dto.excerpt.rendered).ifBlank {
                "Publicación oficial de la Corporación CORPALIV."
            }
            return Noticia(
                id = dto.id,
                titulo = limpiarHtml(dto.title.rendered),
                resumen = resumenLimpio,
                fechaPublicacion = formatearFechaIso(dto.date),
                enlace = dto.link,
                imagenUrl = dto.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
            )
        }
    }

    // Ejecuta la petición HTTP y encapsula el resultado en éxito o fallo controlado
    suspend fun obtenerNoticiasCorpaliv(): Result<List<Noticia>> {
        return try {
            val respuesta = apiService.obtenerNoticias(cantidad = 10, embed = true)
            val noticias = respuesta.map { mapearDtoANoticia(it) }
            Result.success(noticias)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}