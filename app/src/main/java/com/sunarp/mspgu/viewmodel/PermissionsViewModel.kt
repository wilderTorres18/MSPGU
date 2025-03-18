package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.Manifest

data class AppPermissionInfo(
    val packageName: String,
    val permissions: List<String>
)

class PermissionsViewModel : ViewModel() {

    private val _appPermissions = MutableStateFlow<List<AppPermissionInfo>>(emptyList())
    val appPermissions = _appPermissions.asStateFlow()

    fun loadAppPermissions(context: Context) {
        viewModelScope.launch {
            val pm = context.packageManager
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val suspiciousApps = mutableListOf<AppPermissionInfo>()

            installedApps.forEach { app ->
                val packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = packageInfo.requestedPermissions ?: emptyArray()

                val criticalPermissions = permissions.filter { it in listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                )}

                if (criticalPermissions.isNotEmpty()) {
                    suspiciousApps.add(AppPermissionInfo(app.packageName, criticalPermissions))
                }
            }
            _appPermissions.value = suspiciousApps
        }
    }
}
