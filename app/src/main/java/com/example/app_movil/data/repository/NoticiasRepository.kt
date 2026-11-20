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
        // Elimina etiquetas HTML y entidades especiales del texto devuelto por la API
        fun limpiarHtml(textoHtml: String): String {
            return HtmlCompat.fromHtml(textoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .toString()
                .replace("\uFFFC", "")
                .trim()
        }

        // Transforma fecha ISO-8601 del servidor a formato legible dd/MM/yyyy
        fun formatearFechaIso(fechaIso: String): String {
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