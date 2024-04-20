import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import definitions.Actor
import game.Game
import io.github.koalaplot.core.line.*
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberLinearAxisModel
import kotlinx.coroutines.delay
import monkey_sim.composeapp.generated.resources.Res
import monkey_sim.composeapp.generated.resources.pause
import monkey_sim.composeapp.generated.resources.play
import monkey_sim.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.measureTime


@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
  MaterialTheme {
    val game = remember { Game() }

    val paused = remember { mutableStateOf(false) }
    val tick = remember { mutableStateOf(0) }
    val lastTickDuration = remember { mutableStateOf(Duration.ZERO) }
    val maxTickDuration = remember { mutableStateOf(Duration.ZERO) }

    val inspectingActor = remember { mutableStateOf(game.world.actors.first()) }

    LaunchedEffect(key1 = game) {
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
          lastTime = TimeSource.Monotonic.markNow()
        }
      }
    }

    //WeekView(game)
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
      Box(Modifier.fillMaxWidth(0.35f).padding(16.dp)) {
        Column(Modifier.fillMaxSize()) {
          val actor = inspectingActor.value

          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(actor.name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { inspectingActor.value = game.world.actors.random() }) {
              Image(vectorResource(Res.drawable.refresh), null, Modifier.width(16.dp).height(16.dp))
            }
          }

          Text(
            """
            Age: ${actor.age.display()}
            Money: ${actor.money.display()}€
            Workplace: ${actor.workPlace?.let { "$it ${it.work}" } ?: "-"}
            Gender: ${actor.gender}
            ${if (actor.social.partner == null) "Preferred partner: ${actor.preferences.partnerGenderPreference}" else "Partner: ${actor.social.partner}"}            
            Years of education: ${actor.yearsOfEducation.display()}
            State: ${actor.perceivedState}
            Connection sum: ${
              actor.social.connections.entries.sumOf { it.value }.display()
            } (ideal: ${actor.preferences.minConnectionStrengthSum.display()})
            """.trimIndent().trim()
          )

          Spacer(Modifier.weight(1f))

          Divider(Modifier.padding(vertical = 16.dp))

          val lastEntry = game.history.entries.last()

          // Long-time charts

          // Partner percentage
          val partnerPercentageData = game.history.entries.map { entry ->
            Point(
              entry.time.toFloat(),
              100f * entry.peopleWithPartner.toFloat() / entry.worldPopulation.toFloat()
            )
          }
          Row(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 8.dp)) {
            Text("People with partner")
            Spacer(Modifier.weight(1f))
            Text(
              "${lastEntry.peopleWithPartner} / ${lastEntry.worldPopulation} (${(100 * lastEntry.peopleWithPartner.toDouble() / lastEntry.worldPopulation.toDouble()).roundToInt()}%)",
              style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
            )
          }
          Box(Modifier.height(200.dp).padding(end = 8.dp)) {
            XYGraph(
              rememberLinearAxisModel(
                game.history.entries.first().time.toFloat()..lastEntry.time.toFloat() + 0.001f
              ),
              rememberLinearAxisModel(0f..100.0f),
              xAxisTitle = { },
              yAxisTitle = { },
              xAxisLabels = { timestamp ->
                Text(
                  (timestamp.toDouble() % 24).roundToInt().toString() + "h",
                  color = MaterialTheme.colors.onBackground,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(top = 2.dp)
                )
              },
              yAxisLabels = { }
            ) {
              LinePlot(
                partnerPercentageData,
                lineStyle = LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
              )
            }
          }

          Divider(Modifier.padding(vertical = 16.dp))

          // States
          Box(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 8.dp)) {
            Text("State distribution")
          }
          val gameHistory = game.history.entries
            .map { entry -> entry.stateToPeopleCount.values.map { it / entry.worldPopulation.toFloat() }.toList() }
          val transposedGameHistory = gameHistory[0].indices.map { i -> gameHistory.map { it[i] } }

          val stackData: StackedAreaPlotDataAdapter<Float> = StackedAreaPlotDataAdapter(
            game.history.entries.map { it.time.toFloat() },
            transposedGameHistory
          )
          val styles = game.history.entries.first().stateToPeopleCount.keys.map { stateClass ->
            StackedAreaStyle(
              LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
              AreaStyle(SolidColor(Actor.State.colors[stateClass]!!))
            )
          }

          Box(Modifier.height(200.dp).padding(end = 8.dp)) {
            XYGraph(
              rememberLinearAxisModel(
                game.history.entries.first().time.toFloat()..lastEntry.time.toFloat() + 0.001f
              ),
              rememberLinearAxisModel(0f..1.0f),
              xAxisTitle = { },
              yAxisTitle = { },
              xAxisLabels = { timestamp ->
                Text(
                  (timestamp.toDouble() % 24).roundToInt().toString() + "h",
                  color = MaterialTheme.colors.onBackground,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(top = 2.dp)
                )
              },
              yAxisLabels = { }
            ) {
              StackedAreaPlot(
                stackData,
                styles,
                AreaBaseline.ConstantLine(0f),
              )
            }
          }

          val statesList = lastEntry.stateToPeopleCount.entries.reversed()
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            statesList.windowed(
              size = (statesList.size + 2) / 3,
              step = (statesList.size + 2) / 3,
              partialWindows = true
            )
              .forEach { subList ->

                Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                  subList.forEach { (clazz, count) ->
                    val color = Actor.State.colors[clazz]!!
                    Row(
                      Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Box(Modifier.size(16.dp).clip(CircleShape).background(color))
                      Spacer(Modifier.width(8.dp))
                      Text(
                        clazz.simpleName!!,
                        style = LocalTextStyle.current.copy(fontSize = 14.sp),
                      )
                      Spacer(Modifier.width(8.dp))
                      Spacer(Modifier.weight(1f))
                      Text(
                        (100 * count.toDouble() / lastEntry.worldPopulation.toDouble()).roundToInt().toString() + "%",
                        style = LocalTextStyle.current.copy(fontSize = 14.sp, fontFeatureSettings = "tnum"),
                      )
                    }
                    Spacer(Modifier.height(8.dp))
                  }
                }
              }
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
          OutlinedButton(onClick = { paused.value = !paused.value }, modifier = Modifier.padding(end = 16.dp)) {
            if (paused.value) {
              Image(vectorResource(Res.drawable.play), null, Modifier.width(16.dp).height(16.dp))
            } else {
              Image(vectorResource(Res.drawable.pause), null, Modifier.width(16.dp).height(16.dp))
            }
          }

          Text(
            "${game.worldState} - Simulation duration per tick: " +
                "${lastTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0).padStart(5, '0')} - " +
                "max ${maxTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0).padStart(5, '0')}",
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
          )
        }
        Box(Modifier.weight(0.5f).aspectRatio(1f, matchHeightConstraintsFirst = true)) {
          WorldView(inspectingActor.value, game)
        }
      }
    }
  }
}

@Composable
fun WorldView(inspectingActor: Actor, game: Game) {
  DrawWorldOnCanvas(inspectingActor, game, game.world.width, game.world.height)
}