import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.measureTime

val game = Game() // TODO where to put?!?

@Composable
@Preview
fun App() {
  MaterialTheme {
    val tick = remember { mutableStateOf(0) }
    val lastTickDuration = remember { mutableStateOf(Duration.ZERO) }
    val maxTickDuration = remember { mutableStateOf(Duration.ZERO) }

    LaunchedEffect(key1 = game) {
      println("Launching")
      var lastTime = TimeSource.Monotonic.markNow()
      while (true) {
        val renderTime = measureTime {
          game.tick(elapsedHours = lastTime.elapsedNow().inWholeMilliseconds / 300.0)
          tick.value += 1
          lastTime = TimeSource.Monotonic.markNow()
        }

        lastTickDuration.value = renderTime
        maxTickDuration.value = maxOf(maxTickDuration.value, renderTime)

        // 60 FPS, but sleep at least 3ms to avoid 100% CPU
        delay((1000.0 / 60 - renderTime.inWholeMilliseconds).toLong().coerceAtLeast(3))
      }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Row {
        Text(
          "${game.worldState} - ${tick.value} - " +
              "${lastTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0)} - " +
              "max ${maxTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0)}"
        )
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