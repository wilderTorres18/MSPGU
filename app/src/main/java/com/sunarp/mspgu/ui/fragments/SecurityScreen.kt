package com.sunarp.mspgu.ui.fragments

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.DeviceViewModel
import com.sunarp.mspgu.viewmodel.DeviceViewModelFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items



@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SecurityScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DeviceViewModel = viewModel(factory = DeviceViewModelFactory(context))

    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val encryptionStatus by viewModel.encryptionStatus.collectAsState()
    val encryptionMessage by viewModel.encryptionMessage.collectAsState()

    val showInfoDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83D\uDD12 Seguridad del Dispositivo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        content = { paddingValues ->
            LazyColumn( // Cambiamos Column a LazyColumn
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Estado del Cifrado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                }

                item {
                    // Indicador del estado del cifrado
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (encryptionStatus) Color(0xFFDFFFD6) else Color(0xFFFFD6D6)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (encryptionStatus) "✅ Cifrado Activado" else "⚠️ Cifrado No Activado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = encryptionMessage,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (!encryptionStatus) {
                    item {
                        Button(
                            onClick = { navigateToEncryptionSettings(context) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Activar Cifrado")
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = "Información del dispositivo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                }

                // Agregamos las tarjetas como elementos individuales
                items(listOf(
                    Pair(R.drawable.ic_sd_card, "Cifrado del almacenamiento externo" to if (viewModel.checkExternalStorageEncryption()) "✅ Cifrado" else "⚠️ No cifrado"),
                    Pair(R.drawable.ic_key, "Tipo de cifrado" to "AES-256 (Advanced Encryption Standard)"),
                    Pair(R.drawable.ic_storage, "Espacio cifrado" to "${viewModel.getEncryptedStorageDetails().first / (1024 * 1024)} MB de ${viewModel.getEncryptedStorageDetails().second / (1024 * 1024)} MB"),
                    Pair(R.drawable.ic_device, "Modelo" to deviceInfo.deviceName),
                    Pair(R.drawable.ic_factory, "Fabricante" to deviceInfo.manufacturer),
                    Pair(R.drawable.ic_android, "Versión de Android" to deviceInfo.androidVersion),
                    Pair(R.drawable.ic_api, "Nivel de API" to deviceInfo.apiLevel.toString())
                )) { (icon, titleAndValue) ->
                    val (title, value) = titleAndValue
                    DeviceInfoCard(icon = icon, title = title, value = value)
                }

                item {
                    // Botón y mensaje educativo
                    Button(
                        onClick = { showInfoDialog.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("¿Por qué es importante el cifrado?")
                    }

                    if (showInfoDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showInfoDialog.value = false },
                            title = { Text("Importancia del cifrado") },
                            text = {
                                Text(
                                    "El cifrado protege tus datos frente a accesos no autorizados. Configurar un PIN o contraseña asegura que tus datos estén protegidos. " +
                                            "Además, el cifrado ayuda a proteger tu privacidad en caso de pérdida o robo del dispositivo."
                                )
                            },
                            confirmButton = {
                                Button(onClick = { showInfoDialog.value = false }) {
                                    Text("Entendido")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun DeviceInfoCard(icon: Int, title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

fun navigateToEncryptionSettings(context: Context) {
    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun PreviewSecurityScreen() {
    SecurityScreen(
        onNavigateBack = {}
    )
}
