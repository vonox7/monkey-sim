import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    document.querySelector(".lds-ring")?.remove()
    CanvasBasedWindow(canvasElementId = "ComposeTarget") { App() }
}