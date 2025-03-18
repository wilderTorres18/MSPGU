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
import com.sunarp.mspgu.viewmodel.SecurityAuditViewModel
import com.sunarp.mspgu.viewmodel.SecurityAuditViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAuditScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: SecurityAuditViewModel = viewModel(factory = remember { SecurityAuditViewModelFactory(context) })

    val securityChecks by viewModel.securityChecks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔍 Auditoría de Seguridad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.runSecurityAudit() },
                containerColor = Color(0xFFFF5252)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_audit), contentDescription = "Ejecutar Auditoría")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (securityChecks.isEmpty()) {
                Text("🔍 Presiona el botón para iniciar la auditoría.", color = Color.Gray)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(securityChecks) { check ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✅ ${check.title}")
                                Text("📌 ${check.description}", color = Color.Gray)
                                Text(if (check.status) "🟢 Seguro" else "🔴 Riesgoso", color = if (check.status) Color.Green else Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
