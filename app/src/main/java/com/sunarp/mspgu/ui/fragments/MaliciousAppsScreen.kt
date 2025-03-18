package com.sunarp.mspgu.ui.fragments

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.MaliciousAppsViewModel
import com.sunarp.mspgu.viewmodel.MaliciousAppsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaliciousAppsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: MaliciousAppsViewModel = viewModel(factory = remember { MaliciousAppsViewModelFactory(context) })

    val maliciousApps by viewModel.maliciousApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛡️ Protección contra Apps Maliciosas") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.detectMaliciousApps() },
                containerColor = Color(0xFFFF5252)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_security), contentDescription = "Escanear Apps")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (maliciousApps.isEmpty()) {
                Text("✅ No se han detectado aplicaciones sospechosas.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(maliciousApps) { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "📱 ${app.appName}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "⚠️ ${app.reason}", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
