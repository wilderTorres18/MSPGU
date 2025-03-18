package com.sunarp.mspgu.ui.fragments

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.TrackingProtectionViewModel
import com.sunarp.mspgu.viewmodel.TrackingProtectionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingProtectionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: TrackingProtectionViewModel = viewModel(factory = TrackingProtectionViewModelFactory(context))
    val trackingApps by viewModel.trackingApps.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("🛡 Protección de Privacidad", color = Color.White)
                },
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
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 🔍 Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar aplicación...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "📱 Aplicaciones con acceso a ubicación:",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF6200EE)
                )

                if (trackingApps.isEmpty()) {
                    // 🚨 Mensaje si no hay aplicaciones detectadas
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_no_apps),
                                contentDescription = "No hay apps",
                                modifier = Modifier.size(120.dp)
                            )
                            Text("No se detectaron aplicaciones con rastreo.", color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val filteredApps = trackingApps.filter { it.packageName.contains(searchQuery, ignoreCase = true) }

                        items(filteredApps) { app ->
                            TrackingAppCard(
                                packageName = app.packageName,
                                onRevokeClick = { viewModel.revokePermission(app.packageName) }
                            )
                        }
                    }
                }
            }
        }
    )
}

// 📌 Card mejorada con icono y diseño más atractivo
@Composable
fun TrackingAppCard(packageName: String, onRevokeClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "App Icon",
                tint = Color(0xFF6200EE),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = packageName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = "Acceso a ubicación en segundo plano detectado.", color = Color.Gray)
            }

            Button(
                onClick = onRevokeClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
            ) {
                Text(text = "Revocar", color = Color.White)
            }
        }
    }
}
