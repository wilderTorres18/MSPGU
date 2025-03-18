package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecureLocationViewModel(private val context: Context) : ViewModel() {

    private val _isSecureNetwork = MutableStateFlow(true) // Por defecto seguro
    val isSecureNetwork = _isSecureNetwork.asStateFlow()

    init {
        checkNetworkSecurity()
    }

    private fun checkNetworkSecurity() {
        viewModelScope.launch {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            val isSecure = if (isWifi) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val currentSsid = wifiManager.connectionInfo.ssid

                // ⚠️ Lista de redes consideradas "seguras"
                val trustedNetworks = listOf("\"MiRedSegura\"", "\"OficinaCorp\"", "\"HomeSecure\"")
                trustedNetworks.contains(currentSsid)
            } else {
                true // Si no es Wi-Fi, lo consideramos seguro (red móvil)
            }

            _isSecureNetwork.value = isSecure
        }
    }
}
