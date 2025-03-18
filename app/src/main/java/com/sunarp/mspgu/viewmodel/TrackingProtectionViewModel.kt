package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrackingApp(val packageName: String)

class TrackingProtectionViewModel(private val context: Context) : ViewModel() {

    private val _trackingApps = MutableStateFlow<List<TrackingApp>>(emptyList())
    val trackingApps = _trackingApps.asStateFlow()

    init {
        loadTrackingApps()
    }

    private fun loadTrackingApps() {
        viewModelScope.launch {
            val pm = context.packageManager
            val installedPackages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            val trackingList = mutableListOf<TrackingApp>()

            for (packageInfo in installedPackages) {
                val permissions = packageInfo.requestedPermissions ?: emptyArray()

                // 🔹 Verificar si tiene permisos de ubicación en segundo plano
                val hasForegroundLocation = permissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION)
                val hasBackgroundLocation = permissions.contains(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                if (hasForegroundLocation || hasBackgroundLocation) {
                    trackingList.add(TrackingApp(packageInfo.packageName))

                    // 🔍 Mensaje de Log para depuración
                    Log.d("TrackingDetection", "App detectada: ${packageInfo.packageName} | FG: $hasForegroundLocation | BG: $hasBackgroundLocation")
                }
            }

            _trackingApps.value = trackingList
        }
    }

    fun revokePermission(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
