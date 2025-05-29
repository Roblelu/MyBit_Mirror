package com.mybitmirror // o com.mybitmirror.ui.theme si lo tienes allí

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log // Import para Log
import android.view.inputmethod.InputMethodManager
import com.mybitmirror.EmotionEntry // Importar EmotionEntry
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Necesario si EmotionHistoryScreen lo usa y está en otro archivo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mybitmirror.ui.theme.MyBitMirrorTheme

// Objeto para constantes de navegación (buena práctica)
object SettingsDestinations {
    const val EXTRA_START_DESTINATION = "start_destination"
    const val DESTINATION_HISTORY = "history"
    // Otros posibles destinos futuros podrían ir aquí
}

// Enum para las diferentes pantallas dentro de SettingsActivity
enum class SettingsNavigation {
    MAIN_SETTINGS,
    EMOTION_HISTORY,
    ACCOUNT_MANAGEMENT,
    EMOTIONAL_CONNECTIONS,
    DATA_CONSENT
}

private const val ACTIVITY_TAG = "SettingsActivity" // TAG para logs de esta actividad

class SettingsActivity : ComponentActivity() {

    // Estados que necesitan ser actualizados en onResume y leídos por Compose
    private var keyboardEnabledState by mutableStateOf(false)
    private var keyboardSelectedState by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ACTIVITY_TAG, "onCreate called")

        // Determinar la pantalla inicial basada en el Intent
        val startDestinationString = intent.getStringExtra(SettingsDestinations.EXTRA_START_DESTINATION)
        var initialHistoryList: List<EmotionEntry> = emptyList()

        val initialScreen = if (SettingsDestinations.DESTINATION_HISTORY == startDestinationString) {
            Log.d(ACTIVITY_TAG, "Starting with EMOTION_HISTORY due to intent extra.")
            // Recuperar la lista del intent
            val receivedHistory: ArrayList<EmotionEntry>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("emotion_history_data", EmotionEntry::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra("emotion_history_data")
            }
            initialHistoryList = receivedHistory ?: listOf(
                EmotionEntry("Info", "No se pudo cargar el historial desde el teclado.", "Ahora")
            )
            Log.d(ACTIVITY_TAG, "Received history list size: ${initialHistoryList.size}")
            SettingsNavigation.EMOTION_HISTORY
        } else {
            Log.d(ACTIVITY_TAG, "Starting with MAIN_SETTINGS.")
            SettingsNavigation.MAIN_SETTINGS
        }

        // Actualizar estados del teclado aquí también por si acaso onResume no se llama antes de la primera composición
        updateKeyboardStatus()

        setContent {
            MyBitMirrorTheme {
                var currentScreen by rememberSaveable(initialScreen) { mutableStateOf(initialScreen) }
                var currentHistoryList by rememberSaveable(initialHistoryList) { mutableStateOf(initialHistoryList) }


                // Usar los estados a nivel de Actividad para que onResume pueda actualizarlos
                // y Compose reaccione a esos cambios.
                val keyboardEnabled = keyboardEnabledState
                val keyboardSelected = keyboardSelectedState

                Log.d(ACTIVITY_TAG, "setContent: currentScreen = $currentScreen, enabled = $keyboardEnabled, selected = $keyboardSelected")

                if (currentScreen == SettingsNavigation.MAIN_SETTINGS) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Configuración de MyBit Mirror") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    ) { innerPadding ->
                        SettingsScreenContent(
                            modifier = Modifier.padding(innerPadding),
                            isKeyboardEnabled = keyboardEnabled,
                            isKeyboardSelected = keyboardSelected,
                            onEnableKeyboardClick = {
                                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                    // addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // No es necesario desde una Activity
                                })
                            },
                            onSelectKeyboardClick = {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.showInputMethodPicker()
                            },
                            onRefreshStatusClick = {
                                updateKeyboardStatus() // Actualiza los estados de la Actividad
                            },
                            onNavigateToHistory = {
                                // Cuando navegamos desde el menú principal, no tenemos datos del ViewModel
                                // así que usamos una lista de ejemplo o vacía.
                                currentHistoryList = listOf(
                                     EmotionEntry("Info", "Historial completo disponible desde el teclado.", "Ahora")
                                )
                                currentScreen = SettingsNavigation.EMOTION_HISTORY
                            },
                            onNavigateToAccountManagement = {
                                currentScreen = SettingsNavigation.ACCOUNT_MANAGEMENT
                            },
                            onNavigateToEmotionalConnections = {
                                currentScreen = SettingsNavigation.EMOTIONAL_CONNECTIONS
                            },
                            onNavigateToDataConsent = {
                                currentScreen = SettingsNavigation.DATA_CONSENT
                            }
                        )
                    }
                }
                SettingsNavigation.EMOTION_HISTORY -> {
                    EmotionHistoryScreen(
                        historyList = currentHistoryList,
                        onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS }
                    )
                }
                SettingsNavigation.ACCOUNT_MANAGEMENT -> {
                    AccountManagementScreen(
                        onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS }
                    )
                }
                SettingsNavigation.EMOTIONAL_CONNECTIONS -> {
                    EmotionalConnectionsScreen(
                        onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS }
                    )
                }
                SettingsNavigation.DATA_CONSENT -> {
                    DataConsentScreen(
                        onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(ACTIVITY_TAG, "onResume called")
        // Actualizar el estado del teclado cuando la actividad se reanuda
        updateKeyboardStatus()
    }

    private fun updateKeyboardStatus() {
        keyboardEnabledState = isMyBitMirrorEnabled()
        keyboardSelectedState = isMyBitMirrorSelected()
        Log.d(ACTIVITY_TAG, "Updated keyboard status: enabled=$keyboardEnabledState, selected=$keyboardSelectedState")
    }

    private fun isMyBitMirrorEnabled(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enabled = imm.enabledInputMethodList.any {
            it.packageName == packageName && it.serviceName == MyBitMirrorInputMethodService::class.java.name
        }
        Log.d(ACTIVITY_TAG, "isMyBitMirrorEnabled: $enabled")
        return enabled
    }

    private fun isMyBitMirrorSelected(): Boolean {
        val currentImeId = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val myImeId = ComponentName(packageName, MyBitMirrorInputMethodService::class.java.name).flattenToString()
        val selected = currentImeId == myImeId
        Log.d(ACTIVITY_TAG, "isMyBitMirrorSelected: $selected, currentImeId: $currentImeId, myImeId: $myImeId")
        return selected
    }
}

// SettingsScreenContent y SettingsActivityPreview (sin cambios respecto a la última versión que funcionaba,
// solo asegúrate de que SettingsScreenContent tenga el parámetro onNavigateToHistory)
// Re-incluyo SettingsScreenContent aquí para completitud.
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    isKeyboardEnabled: Boolean,
    isKeyboardSelected: Boolean,
    onEnableKeyboardClick: () -> Unit,
    onSelectKeyboardClick: () -> Unit,
    onRefreshStatusClick: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAccountManagement: () -> Unit,
    onNavigateToEmotionalConnections: () -> Unit,
    onNavigateToDataConsent: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        Text(
            text = "Configuración del Teclado MyBit Mirror",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onEnableKeyboardClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isKeyboardEnabled
        ) {
            Text(if (isKeyboardEnabled) "Paso 1: MyBit Mirror HABILITADO ✔️" else "Paso 1: HABILITAR MyBit Mirror")
        }

        Button(
            onClick = onSelectKeyboardClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isKeyboardEnabled && !isKeyboardSelected
        ) {
            Text(if (isKeyboardSelected) "Paso 2: MyBit Mirror SELECCIONADO ✔️" else "Paso 2: SELECCIONAR MyBit Mirror")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Historial de Emociones")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Aumentar espaciado

        Button(
            onClick = onNavigateToAccountManagement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestión de Cuenta/Perfil")
        }

        Button(
            onClick = onNavigateToEmotionalConnections,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestión de Conexiones Emocionales")
        }

        Button(
            onClick = onNavigateToDataConsent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Consentimiento de Datos")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Aumentar espaciado

        Button(
            onClick = onRefreshStatusClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar Estado de Configuración")
        }

        if (isKeyboardEnabled && isKeyboardSelected) {
            Text(
                "¡Teclado listo para usar!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF006400), // Verde oscuro
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsActivityPreview() {
    MyBitMirrorTheme {
        // Puedes previsualizar diferentes estados
        var currentScreen by rememberSaveable { mutableStateOf(SettingsNavigation.MAIN_SETTINGS) }

        if (currentScreen == SettingsNavigation.MAIN_SETTINGS) {
            Scaffold(
                topBar = {
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        title = { Text("Configuración de MyBit Mirror") }
                    )
                }
            ) { innerPadding ->
                SettingsScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    isKeyboardEnabled = true,
                    isKeyboardSelected = false,
                    onEnableKeyboardClick = {},
                    onSelectKeyboardClick = {},
                    onRefreshStatusClick = {},
                    onNavigateToHistory = {
                        // Preview: Navegar al historial con datos de ejemplo
                        // En la app real, esta lambda es manejada por la Activity
                        currentScreen = SettingsNavigation.EMOTION_HISTORY
                    },
                    onNavigateToAccountManagement = { currentScreen = SettingsNavigation.ACCOUNT_MANAGEMENT },
                    onNavigateToEmotionalConnections = { currentScreen = SettingsNavigation.EMOTIONAL_CONNECTIONS },
                    onNavigateToDataConsent = { currentScreen = SettingsNavigation.DATA_CONSENT }
                )
            }
        } else if (currentScreen == SettingsNavigation.EMOTION_HISTORY) {
            // Para el preview, podemos simular una lista aquí
            val previewHistory = listOf(
                EmotionEntry("Info", "Historial de preview.", "Preview Time")
            )
            EmotionHistoryScreen(
                historyList = previewHistory,
                onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS }
            )
        } else if (currentScreen == SettingsNavigation.ACCOUNT_MANAGEMENT) {
            AccountManagementScreen(onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS })
        } else if (currentScreen == SettingsNavigation.EMOTIONAL_CONNECTIONS) {
            EmotionalConnectionsScreen(onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS })
        } else if (currentScreen == SettingsNavigation.DATA_CONSENT) {
            DataConsentScreen(onNavigateBack = { currentScreen = SettingsNavigation.MAIN_SETTINGS })
        }
    }
}

// Nuevas pantallas Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Cuenta") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Aquí se gestionará la cuenta de usuario.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionalConnectionsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Conexiones Emocionales") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Aquí se gestionarán las conexiones emocionales.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataConsentScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consentimiento de Datos") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Aquí se gestionará el consentimiento de datos.")
        }
    }
}