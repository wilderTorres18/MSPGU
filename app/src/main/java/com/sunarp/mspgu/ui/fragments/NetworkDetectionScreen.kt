package com.sunarp.mspgu.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.NetworkDetectionViewModel
import com.sunarp.mspgu.viewmodel.NetworkDetectionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetectionScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: NetworkDetectionViewModel = viewModel(factory = remember { NetworkDetectionViewModelFactory(context) })

    RequestWifiPermissions(viewModel)

    val unsafeNetworks by viewModel.unsafeNetworks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔍 Redes Inseguras") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (unsafeNetworks.isEmpty()) {
                Text("🔍 No se han detectado redes inseguras aún.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(unsafeNetworks) { network ->
                        Text(text = "${network.ssid} - ${network.securityType}")
                    }
                }
            }
        }
    }
}

@Composable
fun RequestWifiPermissions(viewModel: NetworkDetectionViewModel) {
    val context = LocalContext.current
    val permissionGranted = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.NEARBY_WIFI_DEVICES] == true

        if (permissionGranted.value) {
            viewModel.detectUnsafeNetworks() // ✅ Ejecuta la detección después de conceder permisos
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                )
            )
        } else {
            permissionGranted.value = true
            viewModel.detectUnsafeNetworks() // ✅ Ejecuta si ya tiene permisos
        }
    }
}
