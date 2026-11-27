package com.example.app_movil.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

// Utilidades para decodificar bitmaps con downsampling (inSampleSize) y caché LRU en memoria
object BitmapUtils {

    // Caché en memoria acotada (máximo 16 miniaturas muestreadas) para evitar re-lecturas de disco al hacer scroll
    private val imageCache = object : LruCache<String, ImageBitmap>(16) {}

    // Consulta rápida en memoria O(1) antes de lanzar corrutina de I/O
    fun obtenerImagenEnCache(ruta: String): ImageBitmap? = imageCache.get(ruta)

    // Decodifica y guarda en caché un archivo local con downsampling seguro
    fun decodificarYCachearArchivo(
        ruta: String,
        reqWidth: Int,
        reqHeight: Int
    ): ImageBitmap? {
        imageCache.get(ruta)?.let { return it }
        val decodificado = decodificarArchivoMuestreado(ruta, reqWidth, reqHeight)?.asImageBitmap()
        if (decodificado != null) {
            imageCache.put(ruta, decodificado)
        }
        return decodificado
    }

    // Limpia una entrada específica cuando se elimina la solicitud asociada
    fun invalidarCache(ruta: String?) {
        if (!ruta.isNullOrBlank()) {
            imageCache.remove(ruta)
        }
    }

    // Limpia toda la caché al vaciar el historial completo
    fun limpiarCache() {
        imageCache.evictAll()
    }

    // Calcula la potencia de 2 óptima para reducir el tamaño en memoria sin perder nitidez visual
    fun calcularInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    // Decodifica un archivo de imagen local aplicando muestreo para evitar bloqueos y alto uso de RAM
    fun decodificarArchivoMuestreado(
        ruta: String,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return try {
            val archivo = File(ruta)
            if (!archivo.exists()) return null

            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(archivo.absolutePath, boundsOptions)
            if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = calcularInSampleSize(boundsOptions, reqWidth, reqHeight)
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(archivo.absolutePath, decodeOptions)
        } catch (_: Exception) {
            null
        }
    }

    // Decodifica una URI de galería aplicando muestreo en dos pasadas (medición + decodificación reducida)
    fun decodificarUriMuestreada(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return try {
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = calcularInSampleSize(boundsOptions, reqWidth, reqHeight)
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (_: Exception) {
            null
        }
    }
}
