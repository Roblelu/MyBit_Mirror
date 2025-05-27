package com.mybitmirror // O com.mybitmirror.ui.theme si lo dejaste allí

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.mybitmirror.ui.theme.MyBitMirrorTheme // Ajusta si tu tema está en otro lado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionHistoryScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit // Lambda para manejar la acción de "volver atrás"
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
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Aquí se mostrará el historial de emociones.",
                style = MaterialTheme.typography.bodyLarge
            )
            // TODO: Implementar la lista de emociones y justificaciones
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmotionHistoryScreenPreview() {
    MyBitMirrorTheme {
        EmotionHistoryScreen(onNavigateBack = {})
    }
}