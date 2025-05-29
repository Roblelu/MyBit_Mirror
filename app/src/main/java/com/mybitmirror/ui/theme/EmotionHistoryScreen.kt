package com.mybitmirror // O com.mybitmirror.ui.theme si lo dejaste allí

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mybitmirror.EmotionEntry // Importar EmotionEntry desde su nueva ubicación
import com.mybitmirror.ui.theme.MyBitMirrorTheme // Ajusta si tu tema está en otro lado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionHistoryScreen(
    historyList: List<EmotionEntry>,
    onNavigateBack: () -> Unit, // Lambda para manejar la acción de "volver atrás"
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Emociones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (historyList.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay emociones registradas todavía.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList) { emotionEntry ->
                    EmotionHistoryItem(entry = emotionEntry)
                }
            }
        }
    }
}

@Composable
fun EmotionHistoryItem(entry: EmotionEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Emoción: ${entry.emotion}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Justificación: ${entry.justification}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmotionHistoryScreenPreview_WithItems() {
    MyBitMirrorTheme {
        val sampleHistory = listOf(
            EmotionEntry("Felicidad", "Preview: Uso de palabras positivas.", "Hace 1 min"),
            EmotionEntry("Sorpresa", "Preview: ¡Qué sorpresa!", "Hace 5 min")
        )
        EmotionHistoryScreen(historyList = sampleHistory, onNavigateBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EmotionHistoryScreenPreview_Empty() {
    MyBitMirrorTheme {
        EmotionHistoryScreen(historyList = emptyList(), onNavigateBack = {})
    }
}

// Data class EmotionEntry ha sido movida a KeyboardViewModel.kt