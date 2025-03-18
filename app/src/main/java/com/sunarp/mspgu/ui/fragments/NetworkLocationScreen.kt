package com.sunarp.mspgu.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.viewmodel.NetworkLocationViewModel
import com.sunarp.mspgu.viewmodel.NetworkLocationViewModelFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sunarp.mspgu.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkLocationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: NetworkLocationViewModel = viewModel(factory = remember { NetworkLocationViewModelFactory(context) })

    val locationInfo by viewModel.locationInfo.collectAsState()
    val networkInfo by viewModel.networkInfo.collectAsState()
    val locationPermissionGranted = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted.value = isGranted
    }

    // 🔹 Verifica si los permisos ya están concedidos
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        locationPermissionGranted.value = granted
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌍 Análisis de Red y Ubicación", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "📶 Estado de Conexión",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF6200EE))
                )
            }

            item {
                NetworkInfoCard(
                    icon = if (networkInfo.isConnected) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off,
                    title = "Estado de Conexión",
                    value = if (networkInfo.isConnected) "✅ Conectado" else "❌ Desconectado",
                    backgroundColor = if (networkInfo.isConnected) Color(0xFFDFFFD6) else Color(0xFFFFD6D6)
                )
            }

            item {
                NetworkInfoCard(
                    icon = R.drawable.ic_network_type,
                    title = "Tipo de Conexión",
                    value = networkInfo.type
                )
            }

            item {
                NetworkInfoCard(
                    icon = R.drawable.ic_ip_address,
                    title = "Dirección IP",
                    value = networkInfo.ipAddress
                )
            }

            item {
                Text(
                    text = "📍 Información de Ubicación",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF6200EE))
                )
            }

            item {
                LocationInfoCard(
                    icon = R.drawable.ic_location,
                    title = "Ubicación Actual",
                    value = if (locationInfo.isAvailable) "${locationInfo.latitude}, ${locationInfo.longitude}" else "No disponible"
                )
            }

            item {
                if (!locationPermissionGranted.value) {
                    Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                        Text("Solicitar Permiso de Ubicación")
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkInfoCard(icon: Int, title: String, value: String, backgroundColor: Color = Color(0xFFF5F5F5)) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
    }
}

@Composable
fun LocationInfoCard(icon: Int, title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
    }
}
