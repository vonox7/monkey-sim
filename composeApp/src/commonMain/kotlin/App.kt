import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import world.generateSimpleGraz

@Composable
@Preview
fun App() {
  MaterialTheme {
    var showCanvas by remember { mutableStateOf(false) }
    val world = remember { generateSimpleGraz() }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          CanvasDrawer(this, world.width, world.height).draw(world)
        }
      }
    }
  }
}