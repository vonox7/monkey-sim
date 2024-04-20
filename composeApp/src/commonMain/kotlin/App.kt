import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import definitions.Actor
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.measureTime


@Composable
@Preview
fun App() {
  MaterialTheme {
    val game = remember { Game() }

    val paused = remember { mutableStateOf(false) }
    val tick = remember { mutableStateOf(0) }
    val lastTickDuration = remember { mutableStateOf(Duration.ZERO) }
    val maxTickDuration = remember { mutableStateOf(Duration.ZERO) }

    val inspectingActor = remember { mutableStateOf<Actor?>(null) }

    LaunchedEffect(key1 = game) {
      println("Launching")
      var lastTime = TimeSource.Monotonic.markNow()
      while (true) {
        if (!paused.value) {
          val renderTime = measureTime {
          game.tick(elapsedHours = lastTime.elapsedNow().inWholeMilliseconds / 1000.0)
            tick.value += 1
            lastTime = TimeSource.Monotonic.markNow()
          }

          lastTickDuration.value = renderTime
          maxTickDuration.value = maxOf(maxTickDuration.value, renderTime)

          // 60 FPS, but sleep at least 3ms to avoid 100% CPU
          delay((1000.0 / 60 - renderTime.inWholeMilliseconds).toLong().coerceAtLeast(3))
        } else {
          delay((1000.0 / 60).toLong())
        }
      }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(onClick = { paused.value = !paused.value }, modifier = Modifier.padding(end = 16.dp)) {
          Text(if (paused.value) "▶️" else "⏸️")
        }

        Text(
          "${game.worldState} - ${tick.value} - " +
              "${lastTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0)} - " +
              "max ${maxTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0)}"
        )
      }

      Divider()

      //WeekView(game)
      Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Box(Modifier.fillMaxWidth(0.2f).padding(8.dp)) {
          Column {
            Button(onClick = { inspectingActor.value = game.world.actors.random() }) {
              Text("Inspect random actor")
            }

            val actor = inspectingActor.value
            if (actor != null) {
              Text(
                """
              ${actor.name}
              Age: ${actor.age}
              Money: ${actor.money}
              Sex: ${actor.sex}
              Years of education: ${actor.yearsOfEducation.display()}
              Position: ${actor.currentPosition.let { "x: ${it.x.display()}, y: ${it.y.display()}" }}
              Perceived state: ${actor.perceivedState}
              Target: ${actor.targetState}
              
              """.trimIndent()
              )
            }
          }
        }

        VerticalDivider()

        Box(Modifier.weight(0.5f).aspectRatio(1f, matchHeightConstraintsFirst = true)) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            CanvasDrawer(this, game.world.width, game.world.height).draw(game.world)
          }
        }
      }
    }
  }
}