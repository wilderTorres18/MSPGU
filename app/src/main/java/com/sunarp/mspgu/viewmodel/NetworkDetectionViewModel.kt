package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UnsafeNetwork(val ssid: String, val securityType: String)

class NetworkDetectionViewModel(private val context: Context) : ViewModel() {

    private val _unsafeNetworks = MutableStateFlow<List<UnsafeNetwork>>(emptyList())
    val unsafeNetworks = _unsafeNetworks.asStateFlow()

    fun detectUnsafeNetworks() {
        viewModelScope.launch(Dispatchers.IO) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager == null) {
                Log.e("NetworkDetection", "WifiManager no está disponible en este dispositivo.")
                return@launch
            }

            // ✅ Verificar permisos antes de acceder a scanResults
            if (ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("NetworkDetection", "Permiso de ubicación no concedido, no se puede escanear redes WiFi.")
                return@launch
            }

            val scanResults = wifiManager.scanResults ?: emptyList()

            val insecureNetworks = scanResults.filter { isNetworkInsecure(it) }
                .map { UnsafeNetwork(it.SSID.ifEmpty { "Red Desconocida" }, getSecurityType(it)) }

            _unsafeNetworks.value = insecureNetworks

            Log.d("NetworkDetection", "Redes inseguras detectadas: ${_unsafeNetworks.value}")
        }
    }

    private fun isNetworkInsecure(scanResult: ScanResult): Boolean {
        return when {
            scanResult.capabilities.contains("WEP") -> true // 🔴 WEP es inseguro
            scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("WPA2") -> false // ✅ WPA/WPA2 son seguros
            scanResult.capabilities.contains("EAP") -> false // ✅ WPA3 o Enterprise son seguros
            else -> true // 🔴 Sin autenticación, es inseguro
        }
    }

    private fun getSecurityType(scanResult: ScanResult): String {
        return when {
            scanResult.capabilities.contains("WEP") -> "🔴 WEP (Inseguro)"
            scanResult.capabilities.contains("WPA") -> "🟢 WPA/WPA2 (Seguro)"
            scanResult.capabilities.contains("EAP") -> "🟢 WPA3/Enterprise (Seguro)"
            else -> "🔴 Abierta (Sin contraseña)"
        }
    }
}
