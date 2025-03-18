package com.sunarp.mspgu.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sunarp.mspgu.R
import com.sunarp.mspgu.viewmodel.FeedbackViewModel
import com.sunarp.mspgu.viewmodel.FeedbackViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(onNavigateBack: () -> Unit) {
    val viewModel: FeedbackViewModel = viewModel(factory = remember { FeedbackViewModelFactory() })

    val feedbackSent by viewModel.feedbackSent.collectAsState()
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💬 Feedback del Usuario") },
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
            if (feedbackSent) {
                Text("🎉 ¡Gracias por tu feedback!", fontSize = 20.sp, color = Color(0xFF6200EE))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.resetFeedback() }) {
                    Text("Enviar otro comentario")
                }
            } else {
                Text("🌟 ¿Cómo calificarías nuestra aplicación?", fontSize = 18.sp)

                // 🔹 Sección de Estrellas
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Estrella $i",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Caja de Texto para Comentarios
                Text("📝 Deja un comentario (opcional)", fontSize = 16.sp)
                BasicTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { /* Cierra el teclado */ }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(8.dp)
                        .background(Color(0xFFF5F5F5))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Botón para Enviar Feedback
                Button(
                    onClick = { viewModel.submitFeedback(rating, comment) },
                    enabled = rating > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Enviar Feedback", color = Color.White)
                }
            }
        }
    }
}
