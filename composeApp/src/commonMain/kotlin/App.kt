import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import definitions.Actor
import game.Game
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.StackedAreaPlot
import io.github.koalaplot.core.line.StackedAreaPlotDataAdapter
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberLinearAxisModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.measureTime


@OptIn(ExperimentalKoalaPlotApi::class)
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
      println("Launching...")
      var lastTime = TimeSource.Monotonic.markNow()
      while (true) {
        if (!paused.value) {
          val renderTime = measureTime {
            game.tick(elapsedHours = lastTime.elapsedNow().inWholeMilliseconds / 1000.0) // TODO after pausing, this will do a big jump
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

    //WeekView(game)
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
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
              
              Money: ${actor.money.display()}
              
              Sex: ${actor.sex}
              
              Years of education: ${actor.yearsOfEducation.display()}
              
              Position: ${actor.currentPosition.let { "x: ${it.x.display()}, y: ${it.y.display()}" }}
              
              Perceived state: ${actor.perceivedState}
              
              Target: ${actor.targetState}
              
              
              """.trimIndent()
            )
          }

          // History

          val gameHistory = game.history.entries
            .map { entry -> entry.stateToPeopleCount.values.map { it / entry.worldPopulation.toFloat() }.toList() }
          val transposedGameHistory = gameHistory[0].indices.map { i -> gameHistory.map { it[i] } }

          val stackData: StackedAreaPlotDataAdapter<Float> = StackedAreaPlotDataAdapter(
            game.history.entries.map { it.time.toFloat() },
            transposedGameHistory
          )
          // TODO better colors
          val styles = game.history.entries.first().stateToPeopleCount.keys.mapIndexed { index, state ->
            StackedAreaStyle(
              LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
              AreaStyle(SolidColor(Color.Blue.copy(alpha = 0.10f * (index.toFloat() + 1))))
            )
          }

          XYGraph(
            rememberLinearAxisModel(
              game.history.entries.first().time.toFloat()..game.history.entries.last().time.toFloat() + 0.001f
            ),
            rememberLinearAxisModel(0f..1.0f),
            xAxisTitle = "",
            yAxisTitle = "",
            xAxisLabels = { "" },
            yAxisLabels = { it.toDouble().display(1) }
          ) {
            StackedAreaPlot(
              stackData,
              styles,
              AreaBaseline.ConstantLine(0f)
            )
          }
        }
      }

      VerticalDivider()

      Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(onClick = { paused.value = !paused.value }, modifier = Modifier.padding(end = 16.dp)) {
            Text(if (paused.value) "▶️" else "⏸️")
          }

          Text(
            "${game.worldState} - ${tick.value.toString().padStart(5, '0')} - " +
                "${lastTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0)} - " +
                "max ${maxTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0).padStart(2, '0')}",
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
          )
        }

        Box(Modifier.weight(0.5f).aspectRatio(1f, matchHeightConstraintsFirst = true)) {
          DrawWorldOnCanvas(game, game.world.width, game.world.height)
        }
      }
    }
  }
}