package com.sunarp.mspgu.ui.fragments
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.viewmodel.DeviceActivityViewModel
import com.sunarp.mspgu.viewmodel.DeviceActivityViewModelFactory
import androidx.compose.ui.res.painterResource
import com.sunarp.mspgu.R
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceActivityScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: DeviceActivityViewModel = viewModel(factory = remember { DeviceActivityViewModelFactory(context) })

    val deviceActivityLogs by viewModel.deviceActivityLogs.collectAsState()

    // ✅ Verificar si el permiso está concedido
    val hasUsageStatsPermission = remember {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.packageName)
        mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📜 Registro de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasUsageStatsPermission) {
                        viewModel.fetchRecentActivity()
                    } else {
                        // ✅ Abrir la configuración para conceder permisos
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                },
                containerColor = if (hasUsageStatsPermission) Color(0xFFFF5252) else Color(0xFF6200EE)
            ) {
                Text(if (hasUsageStatsPermission) "🔄 Actualizar" else "🔑 Permiso")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasUsageStatsPermission) {
                Text("⚠️ Debes conceder el permiso de 'Acceso a Uso' para ver la actividad del dispositivo.", color = Color.Red)
            } else {
                if (deviceActivityLogs.isEmpty()) {
                    Text("📜 No hay registros recientes de actividad.", color = Color.Gray)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(deviceActivityLogs) { log ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("⏰ ${log.timestamp}")
                                    Text("📱 ${log.packageName}", color = Color.Gray)
                                    Text("🔹 ${log.event}", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
