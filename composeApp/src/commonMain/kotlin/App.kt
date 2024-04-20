import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.TimeSource

val game = Game() // TODO where to put?!?

@Composable
@Preview
fun App() {
  MaterialTheme {
    val tick = remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    scope.launch {
      while (true) {
        val startTime = TimeSource.Monotonic.markNow()
        game.tick()
        tick.value += 1
        // 60 FPS, but sleep at least 3ms to avoid 100% CPU
        delay((1000.0 / 60 - startTime.elapsedNow().inWholeMilliseconds).toLong().coerceAtLeast(3))
      }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Row {
        Text("${game.worldState} - ${tick.value}")
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          CanvasDrawer(this, game.world.width, game.world.height).draw(game.world)
        }
      }
    }
  }
}