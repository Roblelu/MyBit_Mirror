package com.mybitmirror

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Data class para las entradas de emociones (movida desde EmotionHistoryScreen.kt)
@Parcelize
data class EmotionEntry(
    val emotion: String,
    val justification: String,
    val timestamp: String
) : Parcelable

// Enum para los modos de vista del teclado
enum class KeyboardViewMode {
    INPUTTING, // Muestra el teclado QWERTY
    MENU_OPTIONS // Muestra la pantalla de opciones del menú
}

// Data class para el estado emocional del usuario
data class UserEmotionState(
    val primaryEmotion: String = "Neutral", // Emoción dominante
    val justification: String = "Aún no se ha analizado texto." // Justificación de la IA
    // Podrías añadir un mapa para emociones secundarias: val secondaryEmotions: Map<String, Float> = emptyMap()
)

class KeyboardViewModel : ViewModel() {

    private val _currentViewMode = MutableStateFlow(KeyboardViewMode.INPUTTING)
    val currentViewMode: StateFlow<KeyboardViewMode> = _currentViewMode.asStateFlow()

    // State for Symbol Mode (false = QWERTY, true = SYMBOLS)
    private val _isSymbolMode = MutableStateFlow(false)
    val isSymbolMode: StateFlow<Boolean> = _isSymbolMode.asStateFlow()

    // StateFlow para el estado emocional del usuario
    private val _userEmotionState = MutableStateFlow(UserEmotionState())
    val userEmotionState: StateFlow<UserEmotionState> = _userEmotionState.asStateFlow()

    // StringBuilder para el texto actual
    private val currentTextBuffer = StringBuilder()

    // StateFlow para el historial de emociones
    private val _emotionHistory = MutableStateFlow<List<EmotionEntry>>(emptyList())
    val emotionHistory: StateFlow<List<EmotionEntry>> = _emotionHistory.asStateFlow()

    fun setKeyboardViewMode(mode: KeyboardViewMode) {
        _currentViewMode.value = mode
        // Log.d("KeyboardViewModel", "View mode set to: $mode") // Opcional: para depuración
    }

    fun toggleMenuOptions() {
        _currentViewMode.value = if (_currentViewMode.value == KeyboardViewMode.INPUTTING) {
            KeyboardViewMode.MENU_OPTIONS
        } else {
            KeyboardViewMode.INPUTTING
        }
        // Log.d("KeyboardViewModel", "Toggled view mode to: ${_currentViewMode.value}") // Opcional: para depuración
    }

    fun toggleSymbolMode() {
        _isSymbolMode.value = !_isSymbolMode.value
        // Log.d("KeyboardViewModel", "Toggled symbol mode to: ${_isSymbolMode.value}") // Opcional: para depuración
    }

    // Funciones para actualizar el estado emocional
    fun updateUserEmotion(emotion: String, justification: String) {
        _userEmotionState.value = UserEmotionState(emotion, justification)
        // Log.d("KeyboardViewModel", "User emotion updated to: $emotion, Justification: $justification") // Opcional

        // Crear y añadir la entrada al historial
        val currentTime = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date())
        val newEntry = EmotionEntry(
            emotion = emotion,
            justification = justification,
            timestamp = currentTime
        )
        _emotionHistory.value = listOf(newEntry) + _emotionHistory.value
        // Log.d("KeyboardViewModel", "New entry added to history: $newEntry") // Opcional
    }

    fun resetUserEmotionToNeutral() {
        _userEmotionState.value = UserEmotionState()
        // Log.d("KeyboardViewModel", "User emotion reset to Neutral") // Opcional
    }

    // Métodos para manipular el currentTextBuffer
    fun appendChar(char: Char) {
        currentTextBuffer.append(char)
        // Log.d("KeyboardViewModel", "Appended char: $char, Buffer: '${currentTextBuffer.toString()}'") // Opcional
    }

    fun deleteLastChar() {
        if (currentTextBuffer.isNotEmpty()) {
            currentTextBuffer.deleteCharAt(currentTextBuffer.length - 1)
            // Log.d("KeyboardViewModel", "Deleted last char, Buffer: '${currentTextBuffer.toString()}'") // Opcional
        }
    }

    fun getCurrentText(): String {
        return currentTextBuffer.toString()
    }

    fun clearCurrentText() {
        currentTextBuffer.clear()
        // Log.d("KeyboardViewModel", "Text buffer cleared") // Opcional
    }
}
