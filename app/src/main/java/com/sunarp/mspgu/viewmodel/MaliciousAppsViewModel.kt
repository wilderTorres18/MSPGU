package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MaliciousApp(val appName: String, val packageName: String, val reason: String)

class MaliciousAppsViewModel(private val context: Context) : ViewModel() {

    private val _maliciousApps = MutableStateFlow<List<MaliciousApp>>(emptyList())
    val maliciousApps = _maliciousApps.asStateFlow()

    fun detectMaliciousApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val suspiciousApps = mutableListOf<MaliciousApp>()

            for (app in installedApps) {
                val appName = pm.getApplicationLabel(app).toString()
                val packageName = app.packageName

                // 🔴 Condiciones para considerar una app sospechosa
                if (isSuspiciousApp(app, pm)) {
                    suspiciousApps.add(MaliciousApp(appName, packageName, "Permisos peligrosos detectados"))
                } else if (!isFromPlayStore(app, pm)) {
                    suspiciousApps.add(MaliciousApp(appName, packageName, "Aplicación instalada fuera de Google Play"))
                }
            }

            _maliciousApps.value = suspiciousApps

            Log.d("MaliciousDetection", "Apps sospechosas detectadas: ${_maliciousApps.value}")
        }
    }

    private fun isSuspiciousApp(app: ApplicationInfo, pm: PackageManager): Boolean {
        val dangerousPermissions = listOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.WRITE_SETTINGS
        )

        return try {
            val permissions = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
            permissions?.any { it in dangerousPermissions } == true
        } catch (e: Exception) {
            false
        }
    }

    private fun isFromPlayStore(app: ApplicationInfo, pm: PackageManager): Boolean {
        return try {
            val installer = pm.getInstallerPackageName(app.packageName)
            installer == "com.android.vending" // Google Play Store
        } catch (e: Exception) {
            false
        }
    }
}
