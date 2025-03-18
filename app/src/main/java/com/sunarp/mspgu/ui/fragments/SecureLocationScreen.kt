package com.sunarp.mspgu.ui.fragments

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.viewmodel.SecureLocationViewModel
import com.sunarp.mspgu.viewmodel.SecureLocationViewModelFactory
import androidx.compose.ui.unit.dp
import com.sunarp.mspgu.R
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureLocationScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SecureLocationViewModel = viewModel(factory = SecureLocationViewModelFactory(context))

    val isSecureNetwork by viewModel.isSecureNetwork.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔒 Geolocalización Segura", color = Color.White) },
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
            Text(
                text = "Estado de Red",
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                color = Color(0xFF6200EE)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isSecureNetwork) Color(0xFFDFFFD6) else Color(0xFFFFD6D6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isSecureNetwork) "✅ Red Segura" else "⚠️ Red No Segura",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                    )
                    Text(
                        text = if (isSecureNetwork) "Puedes usar GPS con confianza." else "Se recomienda desactivar la ubicación.",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
