package com.sunarp.mspgu.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.sunarp.mspgu.model.LocationInfo
import com.sunarp.mspgu.model.NetworkInfo

class NetworkLocationViewModel(private val context: Context) : ViewModel() {

    // 📍 Estado de la Ubicación
    private val _locationInfo = MutableStateFlow(LocationInfo(0.0, 0.0, false))
    val locationInfo = _locationInfo.asStateFlow()

    // 🌐 Estado de la Conectividad de Red
    private val _networkInfo = MutableStateFlow(NetworkInfo("", "", "", false))
    val networkInfo = _networkInfo.asStateFlow()

    init {
        loadLocationInfo()
        loadNetworkInfo()
    }

    // 📍 Obtener información de ubicación
    private fun loadLocationInfo() {
        viewModelScope.launch {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            var latitude = 0.0
            var longitude = 0.0

            // Verifica si los permisos de ubicación están concedidos
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Obtiene la última ubicación conocida
                val lastLocation: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null) {
                    latitude = lastLocation.latitude
                    longitude = lastLocation.longitude
                }
            }

            _locationInfo.value = LocationInfo(
                latitude = latitude,
                longitude = longitude,
                isAvailable = isGpsEnabled
            )
        }
    }

    // 🌐 Obtener información de red
    private fun loadNetworkInfo() {
        viewModelScope.launch {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo

            val type = when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Móvil"
                else -> "Desconocido"
            }

            _networkInfo.value = NetworkInfo(
                ipAddress = wifiInfo.ipAddress.toString(),
                macAddress = wifiInfo.macAddress ?: "No disponible",
                type = type,
                isConnected = capabilities != null
            )
        }
    }
}
