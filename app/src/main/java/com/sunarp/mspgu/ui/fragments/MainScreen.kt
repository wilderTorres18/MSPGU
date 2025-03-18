package com.sunarp.mspgu.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.sunarp.mspgu.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSecurity: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToSecureLocation: () -> Unit,
    onNavigateToTrackingProtection: () -> Unit,
    onNavigateToNetworkDetection: () -> Unit,
    onNavigateToMaliciousApps: () -> Unit,
    onNavigateToDeviceActivity: () -> Unit,
    onNavigateToSecurityAudit: () -> Unit,
    onNavigateToFeedback: () -> Unit
) {
    val options = listOf(
        OptionData(
            title = "Seguridad y Cifrado de Datos",
            icon = R.drawable.ic_security,
            action = onNavigateToSecurity
        ),
        OptionData(
            title = "Análisis de Red y Ubicación",
            icon = R.drawable.ic_network,
            action = onNavigateToNetwork
        ),
        OptionData(
            title = "Verificación de Permisos",
            icon = R.drawable.ic_permissions,
            action = onNavigateToPermissions
        ),
        OptionData(
            title = "Geolocalización Segura",
            icon = R.drawable.ic_location,
            action = onNavigateToSecureLocation
        ),
        OptionData(
            title = "Protección contra Rastreo No Autorizado",
            icon = R.drawable.ic_tracking,
            action = onNavigateToTrackingProtection
        ),
        // ✅ NUEVAS OPCIONES AÑADIDAS SIN FUNCIONALIDAD
        OptionData(
            title = "Detección de Redes Inseguras",
            icon = R.drawable.ic_wifi_warning,
            action = onNavigateToNetworkDetection
        ),
        OptionData(
            title = "Protección contra Aplicaciones Maliciosas",
            icon = R.drawable.ic_malware,
            action = onNavigateToMaliciousApps
        ),
        OptionData(
            title = "Registro de Actividad del Dispositivo",
            icon = R.drawable.ic_activity_log,
            action = onNavigateToDeviceActivity
        ),
        OptionData(
            title = "Auditoría de Seguridad del Dispositivo",
            icon = R.drawable.ic_audit,
            action = onNavigateToSecurityAudit
        ),
        OptionData(
            title = "Feedback para el Usuario",
            icon = R.drawable.ic_feedback,
            action = onNavigateToFeedback
        )


    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MSPGU - Seguridad Móvil", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(options) { option ->
                    OptionCard(option)
                }
            }
        }
    )
}

@Composable
fun OptionCard(option: OptionData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { option.action() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = option.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Accede para más detalles",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

data class OptionData(
    val title: String,
    val icon: Int,
    val action: () -> Unit
)

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(
        onNavigateToSecurity = {},
        onNavigateToNetwork = {},
        onNavigateToPermissions = {},
        onNavigateToSecureLocation ={},
        onNavigateToTrackingProtection = {},
        onNavigateToNetworkDetection = {},
        onNavigateToMaliciousApps = {},
        onNavigateToDeviceActivity = {},
        onNavigateToSecurityAudit = {},
        onNavigateToFeedback = {}
    )
}
