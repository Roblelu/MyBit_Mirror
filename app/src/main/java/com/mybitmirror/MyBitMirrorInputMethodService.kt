package com.mybitmirror

import android.content.Context // Asegúrate que este import esté si no lo tienes
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.compose.foundation.layout.fillMaxHeight // Ya no lo usaremos en la raíz del teclado
import androidx.compose.foundation.layout.width // Para el Spacer en el menú de iconos
import androidx.compose.material.icons.filled.Abc // Placeholder para Stickers
import androidx.compose.material.icons.filled.EmojiEmotions // Para Emojis
import androidx.compose.material.icons.filled.Gif // Para GIFs (necesitas añadir dependencia si no está)
import androidx.compose.material.icons.filled.ContentCopy // Para Copiar/Pegar
import androidx.compose.material.icons.filled.AccountCircle // Para Perfil
import androidx.compose.material.icons.filled.Palette // Para Temas
import androidx.compose.material.icons.filled.Language // Para Idiomas
import androidx.compose.material.icons.filled.Link // Para Invitar/Conectar


// TAG para los logs.
private const val TAG = "MyBitMirrorService"

// Enum para los modos de vista del teclado (NIVEL DE ARCHIVO)
enum class KeyboardViewMode {
    INPUTTING, // Muestra el teclado QWERTY
    MENU_OPTIONS // Muestra la pantalla de opciones del menú
}

class MyBitMirrorInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var savedStateRegistryController: SavedStateRegistryController

    override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        lifecycleRegistry = LifecycleRegistry(this)
        savedStateRegistryController = SavedStateRegistryController.create(this)
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    private fun setViewTreeOwnersToDecorView() {
        val imeWindow = getWindow()?.window
        if (imeWindow != null) {
            val decorView = imeWindow.decorView
            if (decorView.findViewTreeLifecycleOwner() == null) {
                decorView.setViewTreeLifecycleOwner(this)
                Log.d(TAG, "ViewTreeLifecycleOwner SET on Dialog DecorView")
            }
            if (decorView.findViewTreeViewModelStoreOwner() == null) {
                decorView.setViewTreeViewModelStoreOwner(this)
                Log.d(TAG, "ViewTreeViewModelStoreOwner SET on Dialog DecorView")
            }
            if (decorView.findViewTreeSavedStateRegistryOwner() == null) {
                decorView.setViewTreeSavedStateRegistryOwner(this)
                Log.d(TAG, "ViewTreeSavedStateRegistryOwner SET on Dialog DecorView")
            }
        } else {
            Log.w(TAG, "IME Dialog window is NULL. Cannot set ViewTree owners on decorView at this point.")
        }
    }

    override fun onInitializeInterface() {
        super.onInitializeInterface()
        Log.d(TAG, "onInitializeInterface")
        setViewTreeOwnersToDecorView() // Intentar aquí
    }

    override fun onBindInput() {
        super.onBindInput()
        Log.d(TAG, "onBindInput")
    }

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput - restarting: $restarting, editorInfo: ${attribute?.packageName}")
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView START")
        setViewTreeOwnersToDecorView() // Volver a intentar aquí por si la ventana ya está disponible

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            Log.d(TAG, "ComposeView created")
            setViewTreeLifecycleOwner(this@MyBitMirrorInputMethodService)
            setViewTreeViewModelStoreOwner(this@MyBitMirrorInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@MyBitMirrorInputMethodService)

            setContent {
                Log.d(TAG, "setContent being called")
                MyBitKeyboardScreen(
                    onChar = { char -> sendChar(char) },
                    onDelete = { sendDelete() },
                    onEnter = { sendEnter() }
                )
            }
        }
        Log.d(TAG, "onCreateInputView END - returning ComposeView")
        return composeView
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.d(TAG, "onStartInputView - restarting: $restarting")
    }

    override fun onWindowShown() {
        super.onWindowShown()
        Log.d(TAG, "onWindowShown")
        setViewTreeOwnersToDecorView() // Definitivamente la ventana existe aquí
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        Log.d(TAG, "onWindowHidden")
    }

    private fun sendChar(char: Char) {
        Log.d(TAG, "sendChar: $char")
        currentInputConnection?.commitText(char.toString(), 1)
    }

    private fun sendDelete() {
        Log.d(TAG, "sendDelete")
        sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DEL)
    }

    private fun sendEnter() {
        Log.d(TAG, "sendEnter")
        val editorInfo = currentInputEditorInfo
        if (editorInfo != null && editorInfo.actionId != 0 && editorInfo.actionId != android.view.inputmethod.EditorInfo.IME_ACTION_NONE) {
            currentInputConnection?.performEditorAction(editorInfo.actionId)
        } else {
            currentInputConnection?.commitText("\n", 1)
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        Log.d(TAG, "onFinishInputView - finishingInput: $finishingInput")
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
} // FIN DE LA CLASE MyBitMirrorInputMethodService


// --- FUNCIONES COMPOSABLE (FUERA DE LA CLASE) ---

@Composable
fun TopBar(
    context: Context, // Usar Context de android.content
    onRhombusClick: () -> Unit, // Mantenida por si se usa para algo más que lanzar intent
    onDictationClick: () -> Unit,
    onMenuIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            Log.d(TAG, "Rhombus Clicked - Launching SettingsActivity for History")
            val intent = Intent(context, SettingsActivity::class.java).apply {
                putExtra(SettingsDestinations.EXTRA_START_DESTINATION, SettingsDestinations.DESTINATION_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            // onRhombusClick() // Llama si es necesario
        }) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Historial de Emociones",
                modifier = Modifier.size(28.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Sugerencias...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        IconButton(onClick = onDictationClick) {
            Icon(Icons.Filled.Mic, contentDescription = "Dictado por voz", modifier = Modifier.size(24.dp))
        }

        IconButton(onClick = onMenuIconClick) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Menú")
        }
    }
}

@Composable
fun MyBitKeyboardScreen(
    onChar: (Char) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit
) {
    Log.d(TAG, "MyBitKeyboardScreen Composable executing")
    val context = LocalContext.current
    var currentViewMode by remember { mutableStateOf(KeyboardViewMode.INPUTTING) }

    Surface(
        modifier = Modifier.fillMaxWidth(), // Quitamos .fillMaxHeight() para que la altura sea wrap_content
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), // Quitamos .fillMaxSize(), la altura será wrap_content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                context = context,
                onRhombusClick = {
                    Log.d(TAG, "Rhombus Clicked - Launching SettingsActivity for History")
                    val intent = Intent(context, SettingsActivity::class.java).apply {
                        putExtra(SettingsDestinations.EXTRA_START_DESTINATION, SettingsDestinations.DESTINATION_HISTORY)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                onDictationClick = { Log.d(TAG, "Dictation Clicked") },
                onMenuIconClick = {
                    currentViewMode = if (currentViewMode == KeyboardViewMode.INPUTTING) {
                        KeyboardViewMode.MENU_OPTIONS
                    } else {
                        KeyboardViewMode.INPUTTING // Permitir volver al teclado si se toca de nuevo
                    }
                    Log.d(TAG, "Menu icon clicked, changing view to $currentViewMode")
                }
            )

            // Spacer(modifier = Modifier.height(4.dp)) // Opcional, podemos quitarlo si el padding interno es suficiente

            when (currentViewMode) {
                KeyboardViewMode.INPUTTING -> {
                    KeyboardInputArea(
                        onChar = onChar,
                        onDelete = onDelete,
                        onEnter = onEnter
                    )
                }
                KeyboardViewMode.MENU_OPTIONS -> {
                    MenuOptionsScreen(
                        context = context,
                        onNavigateBackToKeyboard = {
                            currentViewMode = KeyboardViewMode.INPUTTING
                            Log.d(TAG, "Navigating back to INPUTTING view")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardInputArea(
    onChar: (Char) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit
) {
    val row1Keys = "QWERTYUIOP"
    val row2Keys = "ASDFGHJKL"
    val row3Keys = "ZXCVBNM"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        KeyboardRow(keys = row1Keys, onChar = onChar, keyAspectRatio = 0.8f)
        KeyboardRow(keys = row2Keys, onChar = onChar, modifier = Modifier.padding(horizontal = 10.dp), keyAspectRatio = 0.8f)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(text = "↑", onClick = { Log.d(TAG, "Shift Clicked") }, modifier = Modifier.weight(1.5f).aspectRatio(0.8f))
            row3Keys.forEach { char ->
                KeyButton(text = char.toString(), onClick = { onChar(char) }, modifier = Modifier.weight(1f).aspectRatio(0.8f))
            }
            KeyButton(text = "⌫", onClick = onDelete, modifier = Modifier.weight(1.5f).aspectRatio(0.8f))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyButton(text = "?123", onClick = { Log.d(TAG, "?123 Clicked") }, modifier = Modifier.weight(2f).aspectRatio(1.1f))
            KeyButton(text = ",", onClick = { onChar(',') }, modifier = Modifier.weight(1f).aspectRatio(1.1f))
            KeyButton(text = "ESPACIO", onClick = { onChar(' ') }, modifier = Modifier.weight(4f).aspectRatio(1.1f))
            KeyButton(text = ".", onClick = { onChar('.') }, modifier = Modifier.weight(1f).aspectRatio(1.1f))
            KeyButton(text = "↵", onClick = onEnter, modifier = Modifier.weight(2f).aspectRatio(1.1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOptionsScreen(
    context: Context,
    onNavigateBackToKeyboard: () -> Unit
) {
    // El Scaffold aquí es opcional si la TopBar principal siempre está visible.
    // Si queremos que esta vista tenga su propia barra superior (quizás solo con "Atrás"),
    // el Scaffold es útil. Si no, podemos usar directamente LazyColumn.
    // Por ahora, lo mantendremos simple, asumiendo que la TopBar principal sigue siendo visible y funcional.
    // El botón de tres puntos en la TopBar ahora también servirá para cerrar este menú si está abierto.

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth() // Ocupará el ancho disponible
            // La altura será determinada por el contenido o la altura restante del IME.
            // Si queremos que siempre tenga una altura específica o llene el espacio,
            // se podría añadir .weight(1f) si está dentro de una Column padre que lo permita,
            // o un .heightIn(max = ...)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Placeholder para los iconos, reemplaza con los que correspondan
        val menuItems = listOf(
            MenuItemData(Icons.Filled.Abc, "Stickers") { Log.d(TAG, "Stickers Clicked") },
            MenuItemData(Icons.Filled.EmojiEmotions, "Emojis") { Log.d(TAG, "Emojis Clicked") },
            MenuItemData(Icons.Filled.Gif, "GIFs") { Log.d(TAG, "GIFs Clicked") },
            MenuItemData(Icons.Filled.ContentCopy, "Copiar/Pegar") { Log.d(TAG, "Copiar/Pegar Clicked") },
            MenuItemData(Icons.Filled.AccountCircle, "Perfil de Usuario") {
                Log.d(TAG, "Menu: Perfil de Usuario Clicked")
                val intent = Intent(context, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            MenuItemData(Icons.Filled.Palette, "Temas del teclado") {
                Log.d(TAG, "Menu: Temas del teclado Clicked")
                val intent = Intent(context, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            MenuItemData(Icons.Filled.Language, "Idiomas") {
                Log.d(TAG, "Menu: Idiomas Clicked")
                val intent = Intent(context, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            MenuItemData(Icons.Filled.Link, "Invitar / Conectar") { Log.d(TAG, "Invitar Clicked") }
        )

        items(menuItems.size) { index ->
            val item = menuItems[index]
            MenuOptionItem(
                icon = item.icon,
                text = item.text,
                onClick = {
                    item.action()
                    // Opcionalmente, siempre volver al teclado después de una acción de menú
                    // onNavigateBackToKeyboard()
                }
            )
        }
    }
}

// Helper data class para los ítems del menú
data class MenuItemData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val text: String,
    val action: () -> Unit
)
@Composable
fun MenuOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button( // O puedes usar un Row con clickable para un diseño más personalizado
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(Modifier.width(8.dp))
            Text(text)
        }
    }
}
@Composable
fun KeyboardRow(
    keys: String,
    onChar: (Char) -> Unit,
    modifier: Modifier = Modifier,
    keyAspectRatio: Float = 0.8f
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        keys.forEach { char ->
            KeyButton(
                text = char.toString(),
                onClick = { onChar(char) },
                modifier = Modifier.weight(1f).aspectRatio(keyAspectRatio)
            )
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = when {
                text.length > 1 && (text == "ESPACIO" || text == "?123") -> 12.sp
                text.length == 1 && text[0].isLetterOrDigit() -> 18.sp
                else -> 16.sp
            },
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}