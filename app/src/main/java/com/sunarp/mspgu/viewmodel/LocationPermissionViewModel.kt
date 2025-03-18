package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationPermissionViewModel(private val context: Context) : ViewModel() {

    private val _appPermissions = MutableStateFlow<List<AppPermissionInfo>>(emptyList())
    val appPermissions = _appPermissions.asStateFlow()

    init {
        loadAppsWithLocationPermission()
    }

    private fun loadAppsWithLocationPermission() {
        viewModelScope.launch {
            val packageManager = context.packageManager
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val suspiciousApps = mutableListOf<AppPermissionInfo>()

            installedApps.forEach { app ->
                val packageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = packageInfo.requestedPermissions ?: emptyArray()

                // Filtra los permisos críticos que deseas evaluar
                val criticalPermissions = permissions.filter {
                    it == android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                }

                // Si la lista de permisos críticos no está vacía, agrega la aplicación
                if (criticalPermissions.isNotEmpty()) {
                    suspiciousApps.add(AppPermissionInfo(app.packageName, criticalPermissions))
                }
            }

            _appPermissions.value = suspiciousApps
        }
    }

    fun openAppSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = android.net.Uri.parse("package:$packageName")
        context.startActivity(intent)
    }
}
