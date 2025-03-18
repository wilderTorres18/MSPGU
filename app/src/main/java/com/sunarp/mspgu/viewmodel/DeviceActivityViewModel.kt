package com.sunarp.mspgu.viewmodel

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DeviceActivityLog(val timestamp: String, val event: String, val packageName: String)

class DeviceActivityViewModel(private val context: Context) : ViewModel() {

    private val _deviceActivityLogs = MutableStateFlow<List<DeviceActivityLog>>(emptyList())
    val deviceActivityLogs = _deviceActivityLogs.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun fetchRecentActivity() {
        viewModelScope.launch(Dispatchers.IO) {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            if (usageStatsManager == null) {
                Log.e("DeviceActivity", "UsageStatsManager no está disponible en este dispositivo.")
                return@launch
            }

            val currentTime = System.currentTimeMillis()
            val oneHourAgo = currentTime - (60 * 60 * 1000) // Última hora

            val events = usageStatsManager.queryEvents(oneHourAgo, currentTime)
            val eventList = mutableListOf<DeviceActivityLog>()
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            val event = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                val eventType = when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> "Aplicación Abierta"
                    UsageEvents.Event.ACTIVITY_PAUSED -> "Aplicación Cerrada"
                    else -> "Otro Evento"
                }

                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED || event.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    eventList.add(DeviceActivityLog(dateFormat.format(Date(event.timeStamp)), eventType, event.packageName))
                }
            }

            _deviceActivityLogs.value = eventList
            Log.d("DeviceActivity", "Eventos detectados: ${_deviceActivityLogs.value}")
        }
    }
}
