import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import definitions.Actor
import game.Game
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import kotlinx.coroutines.delay
import monkey_sim.composeapp.generated.resources.Res
import monkey_sim.composeapp.generated.resources.pause
import monkey_sim.composeapp.generated.resources.play
import monkey_sim.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.charts.occupation.ActorStatesChart
import ui.charts.occupation.OccupationChart
import ui.charts.occupation.PartnerChart
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
            Strongest connection: ${
              actor.social.connections.maxByOrNull { it.value }?.let { "${it.key}: ${it.value.display()}" } ?: "-"
            }
            Connection sum: ${
              actor.social.connections.entries.sumOf { it.value }.display()
            } (ideal: ${actor.preferences.minConnectionStrengthSum.display()})
            """.trimIndent().trim(),
            style = LocalTextStyle.current.copy(fontSize = 12.sp, lineHeight = 17.sp),
          )

          Spacer(Modifier.weight(1f))

          Divider(Modifier.padding(vertical = 16.dp))

          // Charts
          PartnerChart(game.history)
          Divider(Modifier.padding(vertical = 10.dp))
          OccupationChart(game.history)
          Divider(Modifier.padding(vertical = 16.dp))
          ActorStatesChart(game.history)
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
            "${game.worldState} - ${tick.value} ticks - Simulation duration per tick: " +
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