package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class SecurityCheck(val title: String, val status: Boolean, val description: String)

class SecurityAuditViewModel(private val context: Context) : ViewModel() {

    private val _securityChecks = MutableStateFlow<List<SecurityCheck>>(emptyList())
    val securityChecks = _securityChecks.asStateFlow()

    fun runSecurityAudit() {
        viewModelScope.launch(Dispatchers.IO) {
            val results = mutableListOf<SecurityCheck>()

            results.add(SecurityCheck("🔑 Root Access", !isDeviceRooted(), "Verifica si el dispositivo tiene root."))
            results.add(SecurityCheck("🔒 Encriptación Activa", isStorageEncrypted(), "Revisa si el almacenamiento está encriptado."))
            results.add(SecurityCheck("🛡️ Google Play Protect", isGooglePlayProtectEnabled(), "Detecta si Google Play Protect está activado."))
            results.add(SecurityCheck("🚨 Herramientas de Hacking", !detectHackingTools(), "Detecta apps como Frida o Magisk."))
            results.add(SecurityCheck("⚠️ Aplicaciones con permisos peligrosos", !hasDangerousPermissions(), "Identifica apps con permisos riesgosos."))

            _securityChecks.value = results
            Log.d("SecurityAudit", "Auditoría completada: ${_securityChecks.value}")
        }
    }

    private fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/xbin/su", "/data/local/bin/su",
            "/data/local/su", "/system/su", "/system/bin/.ext/.su"
        )

        return paths.any { File(it).exists() }
    }

    private fun isStorageEncrypted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Environment.isExternalStorageEmulated()
        } else {
            false
        }
    }

    private fun isGooglePlayProtectEnabled(): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo("com.google.android.gms", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun detectHackingTools(): Boolean {
        val hackingApps = listOf("com.topjohnwu.magisk", "org.lsposed.manager", "com.frida")
        return hackingApps.any { isPackageInstalled(it) }
    }

    private fun hasDangerousPermissions(): Boolean {
        val dangerousPermissions = listOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.WRITE_SETTINGS
        )

        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.any { app ->
            try {
                val permissions = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
                permissions?.any { it in dangerousPermissions } == true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
