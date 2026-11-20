package com.example.app_movil.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SimuladorViewModel : ViewModel() {

    // Valores referenciales y fórmulas puras (compartidas con ExampleUnitTest)
    companion object {
        const val MONTO_MINIMO_SIMULACION = 5000L
        const val COSTO_REFERENCIAL_KIT_INSUMOS = 5000L
        const val COSTO_REFERENCIAL_SESION_APOYO = 15000L

        fun calcularKitsInsumos(montoClp: Long): Int {
            return if (montoClp >= MONTO_MINIMO_SIMULACION) {
                (montoClp / COSTO_REFERENCIAL_KIT_INSUMOS).toInt()
            } else 0
        }

        fun calcularSesionesApoyo(montoClp: Long): Int {
            return if (montoClp >= COSTO_REFERENCIAL_SESION_APOYO) {
                (montoClp / COSTO_REFERENCIAL_SESION_APOYO).toInt()
            } else 0
        }

        fun generarTextoImpactoSocial(montoClp: Long): String {
            val kitsInsumos = calcularKitsInsumos(montoClp)
            val sesionesApoyo = calcularSesionesApoyo(montoClp)
            return "Con un aporte de \$$montoClp CLP financias aproximadamente $kitsInsumos kits de insumos para el taller laboral" +
                    if (sesionesApoyo > 0) " y $sesionesApoyo sesiones de apoyo sociolaboral." else "."
        }
    }

    // Estados observables del monto ingresado, error y texto de resultado
    var montoSimulado by mutableStateOf("")
        private set

    var montoSimuladoError by mutableStateOf<String?>(null)
        private set

    var resultadoImpacto by mutableStateOf<String?>(null)
        private set

    // Valida en tiempo real que el monto contenga solo números y sea >= $5.000 CLP
    fun onMontoSimuladoChange(nuevoMonto: String) {
        montoSimulado = nuevoMonto
        resultadoImpacto = null
        if (nuevoMonto.isEmpty()) {
            montoSimuladoError = null
            return
        }
        if (!nuevoMonto.all { it.isDigit() }) {
            montoSimuladoError = "Ingrese únicamente números sin puntos ni símbolos."
        } else {
            val valor = nuevoMonto.toLongOrNull() ?: 0L
            montoSimuladoError = if (valor < MONTO_MINIMO_SIMULACION) {
                "El aporte mínimo para simulación es de $5.000 CLP."
            } else {
                null
            }
        }
    }

    // Ejecuta el cálculo final y muestra el mensaje de impacto social
    fun calcularImpactoSocial() {
        val valor = montoSimulado.toLongOrNull()
        if (valor == null || valor < MONTO_MINIMO_SIMULACION) {
            montoSimuladoError = "Ingrese un monto válido igual o superior a $5.000 CLP."
            resultadoImpacto = null
        } else {
            resultadoImpacto = generarTextoImpactoSocial(valor)
        }
    }
}