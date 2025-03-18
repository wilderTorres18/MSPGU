package com.sunarp.mspgu.model

data class NetworkInfo(
    val ipAddress: String = "0.0.0.0",
    val macAddress: String = "00:00:00:00:00:00",
    val networkType: String = "Unknown",
    val isConnected: Boolean = false,
    val status: String = "Desconectado",
    val type: String = "unknown",
)

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val isAvailable: Boolean
)
