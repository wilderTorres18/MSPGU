package com.sunarp.mspgu.ui.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sunarp.mspgu.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.viewmodel.LocationPermissionViewModel
import com.sunarp.mspgu.viewmodel.LocationPermissionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LocationPermissionViewModel = viewModel(factory = LocationPermissionViewModelFactory(context))

    val appPermissions by viewModel.appPermissions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔍 Verificación de Permisos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("📌 Aplicaciones con acceso a la ubicación en segundo plano", style = MaterialTheme.typography.headlineSmall)

            if (appPermissions.isEmpty()) {
                Text("✅ No hay aplicaciones sospechosas con acceso en segundo plano.", modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appPermissions) { app ->
                        AppPermissionCard(app.packageName) {
                            viewModel.openAppSettings(app.packageName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppPermissionCard(packageName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "📲 $packageName", style = MaterialTheme.typography.titleMedium)
            Text(text = "🔍 Accede a la ubicación en segundo plano", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
