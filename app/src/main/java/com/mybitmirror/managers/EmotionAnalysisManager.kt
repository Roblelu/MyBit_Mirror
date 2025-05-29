package com.mybitmirror.managers

import kotlinx.coroutines.delay

// Estructura de datos para la respuesta de la IA (Mockup)
data class EmotionAnalysisResult(
    val emotions: Map<String, Float>, // Ej. {"felicidad": 0.8f, "tristeza": 0.1f}
    val dominantEmotion: String,
    val justification: String
)

class EmotionAnalysisManager {

    suspend fun analyzeEmotionMock(text: String): EmotionAnalysisResult {
        // Simula un pequeño retraso como si fuera una llamada de red
        delay(500) // 500 ms de retraso

        // Lógica de mockup simple:
        // Podrías tener algunas palabras clave para devolver diferentes emociones.
        return when {
            text.contains("feliz", ignoreCase = true) || text.contains("😄", ignoreCase = true) -> EmotionAnalysisResult(
                emotions = mapOf("felicidad" to 0.9f, "calma" to 0.2f),
                dominantEmotion = "Felicidad",
                justification = "El texto contiene la palabra 'feliz' y/o emojis positivos."
            )
            text.contains("triste", ignoreCase = true) || text.contains("😔", ignoreCase = true) -> EmotionAnalysisResult(
                emotions = mapOf("tristeza" to 0.85f, "ansiedad" to 0.3f),
                dominantEmotion = "Tristeza",
                justification = "El texto expresa sentimientos de tristeza."
            )
            text.contains("sorpresa", ignoreCase = true) || text.contains("😮", ignoreCase = true) -> EmotionAnalysisResult(
                emotions = mapOf("sorpresa" to 0.9f, "interés" to 0.4f),
                dominantEmotion = "Sorpresa",
                justification = "El texto indica sorpresa o asombro."
            )
            text.isBlank() -> EmotionAnalysisResult( // Caso para texto vacío o muy corto
                emotions = mapOf("neutral" to 1.0f),
                dominantEmotion = "Neutral",
                justification = "No hay suficiente texto para analizar."
            )
            else -> EmotionAnalysisResult(
                emotions = mapOf("neutral" to 0.6f, "interés" to 0.3f),
                dominantEmotion = "Neutral",
                justification = "El análisis simulado no encontró una emoción clara, predominando la neutralidad."
            )
        }
    }
}
