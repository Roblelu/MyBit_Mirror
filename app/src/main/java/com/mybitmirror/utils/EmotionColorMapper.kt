package com.mybitmirror.utils

import androidx.compose.ui.graphics.Color

object EmotionColorMapper {
    private val emotionColorMap: Map<String, Color> = mapOf(
        "Felicidad" to Color(0xFFFFD700), // Amarillo brillante (Oro)
        "Tristeza" to Color(0xFFADD8E6),  // Azul claro (LightBlue)
        "Sorpresa" to Color(0xFF90EE90),  // Verde claro (LightGreen)
        "Ira" to Color(0xFFFF6347),      // Rojo tomate (Tomato)
        "Calma" to Color(0xFFB0E0E6),    // Azul pálido (PowderBlue)
        "Ansiedad" to Color(0xFFDA70D6),  // Orquídea
        "Interés" to Color(0xFFFFA500),   // Naranja
        "Neutral" to Color.LightGray,
        "Default" to Color.Gray // Un color por defecto si la emoción no se encuentra
    )

    fun getColorForEmotion(emotion: String): Color {
        return emotionColorMap[emotion] ?: emotionColorMap["Default"]!!
    }
}
