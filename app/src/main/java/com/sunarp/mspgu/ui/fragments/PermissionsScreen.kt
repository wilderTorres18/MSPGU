package com.sunarp.mspgu.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.PermissionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PermissionsViewModel = viewModel()
    val appPermissions by viewModel.appPermissions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔐 Auditoría de Permisos", color = Color.White) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📋 Aplicaciones y sus permisos", style = MaterialTheme.typography.headlineSmall)

            if (appPermissions.isEmpty()) {
                Text("No se encontraron permisos sospechosos.", color = Color.Gray)
            } else {
                appPermissions.forEach { app ->
                    AppPermissionsCard(app.packageName, app.permissions, context)
                }
            }

            Button(
                onClick = { context.startActivity(Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Revisar permisos manualmente")
            }
        }
    }
}

@Composable
fun AppPermissionsCard(packageName: String, permissions: List<String>, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("📱 $packageName", style = MaterialTheme.typography.bodyMedium)
            permissions.forEach { permission ->
                Text("- $permission", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Administrar permisos")
            }
        }
    }
}
