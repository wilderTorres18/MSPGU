package com.sunarp.mspgu.viewmodel

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.sunarp.mspgu.model.DeviceInfo
import android.app.KeyguardManager
import android.os.StatFs


class DeviceViewModel(private val context: Context) : ViewModel() {

    private val _deviceInfo = MutableStateFlow(DeviceInfo())
    val deviceInfo = _deviceInfo.asStateFlow()

    private val _encryptionStatus = MutableStateFlow(false)
    val encryptionStatus = _encryptionStatus.asStateFlow()

    private val _encryptionMessage = MutableStateFlow("")
    val encryptionMessage = _encryptionMessage.asStateFlow()

    init {
        loadDeviceInfo()
        checkEncryptionStatus()
    }

    private fun loadDeviceInfo() {
        viewModelScope.launch {
            _deviceInfo.value = DeviceInfo(
                deviceName = Build.MODEL,
                manufacturer = Build.MANUFACTURER,
                androidVersion = Build.VERSION.RELEASE,
                apiLevel = Build.VERSION.SDK_INT
            )
        }
    }

    fun checkEncryptionStatus() {
        viewModelScope.launch {
            try {
                val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

                val hasSecureLockScreen = keyguardManager.isDeviceSecure // Verifica si hay PIN, patrón o contraseña

                val isEncrypted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    storageManager.storageVolumes.any { it.state == Environment.MEDIA_MOUNTED }
                } else {
                    Environment.isExternalStorageEmulated()
                }

                // Solo considera cifrado activado si hay un método de bloqueo seguro
                _encryptionStatus.value = isEncrypted && hasSecureLockScreen
                _encryptionMessage.value = if (isEncrypted && hasSecureLockScreen) {
                    "Cifrado activado y seguro."
                } else if (!hasSecureLockScreen) {
                    "Cifrado no activado porque no tienes un PIN, patrón o contraseña configurado."
                } else {
                    "Cifrado no activado. Activa el cifrado para proteger tus datos."
                }
            } catch (e: Exception) {
                _encryptionStatus.value = false
                _encryptionMessage.value = "Error al verificar el cifrado: ${e.message}"
            }
        }
    }

    private fun StorageManager.isEncrypted(): Boolean {
        return try {
            this.storageVolumes.any { it.state == Environment.MEDIA_MOUNTED && this.isEncrypted() }
        } catch (e: Exception) {
            false
        }
    }

    fun checkExternalStorageEncryption(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED && Environment.isExternalStorageEmulated()
    }
    fun getEncryptedStorageDetails(): Pair<Long, Long> {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalBytes = statFs.totalBytes
        val freeBytes = statFs.availableBytes
        val usedBytes = totalBytes - freeBytes

        return Pair(usedBytes, totalBytes) // Retorna espacio usado y total
    }
    fun isStrongAuthConfigured(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isDeviceSecure // Retorna true si hay un bloqueo fuerte configurado
    }

}
