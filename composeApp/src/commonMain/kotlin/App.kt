import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import definitions.Actor
import game.Game
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


@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
  MaterialTheme {
    val game = remember { Game() }

    val paused = remember { mutableStateOf(false) }
    val speed = remember { mutableStateOf(Speed.NORMAL) }
    val tick = remember { mutableStateOf(0) }
    val lastTimesSimulationTookTooLong: MutableState<MutableList<TimeSource.Monotonic.ValueTimeMark>> =
      remember { mutableStateOf(mutableListOf()) }
    val lastTimesSimulationTookTooLongSize = 5
    val lastTickDuration = remember { mutableStateOf(Duration.ZERO) }
    val maxTickDuration = remember { mutableStateOf(Duration.ZERO) }

    val inspectingActor = remember { mutableStateOf(game.world.actors.first()) }

    LaunchedEffect(key1 = game) {
      var lastTime = TimeSource.Monotonic.markNow()
      while (true) {
        if (!paused.value) {
          val renderTime = measureTime {
            val elapsedMs = lastTime.elapsedNow().inWholeMilliseconds
              .coerceAtMost(100) // Limit to 100ms to not jump too far ahead when debugging (or the game was paused for another reason)
            repeat(speed.value.factor) { // When we do double speed we want to do twice the ticks per frame, but still ensure the simulation runs the same
              game.tick(elapsedHours = elapsedMs / 1000.0)
              tick.value += 1
            }
            lastTime = TimeSource.Monotonic.markNow()
          }

          lastTickDuration.value = renderTime
          maxTickDuration.value = maxOf(maxTickDuration.value, renderTime)
          if (renderTime.inWholeMilliseconds >= (1000.0 / 60 - 3)) {
            lastTimesSimulationTookTooLong.value.add(lastTime)
            // Make sure we don't keep all the values forever
            if (lastTimesSimulationTookTooLong.value.size > lastTimesSimulationTookTooLongSize) {
              lastTimesSimulationTookTooLong.value.removeAt(0)
            }
          }

          // 60 FPS, but sleep at least 3ms to avoid 100% CPU
          delay((1000.0 / 60 - renderTime.inWholeMilliseconds).toLong().coerceAtLeast(3))
        } else {
          delay((1000.0 / 60).toLong())
          lastTime = TimeSource.Monotonic.markNow()
        }
      }
    }

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
            Workplace: ${actor.workPlace?.let { "$it ${it.work}" } ?: "[${actor.workingCategory.chartName}]"}
            Gender: ${actor.gender}
            ${
              if (actor.social.partner == null) {
                if (actor.partnerAgePreference == null) {
                  "No preferred partner yet"
                } else {
                  "Preferred partner: ${actor.preferences.partnerGenderPreference} with age ${actor.partnerAgePreference}"
                }
              } else {
                "Partner: ${actor.social.partner}"
              }
            }            
            Years of education: ${actor.yearsOfEducation.display()}
            State: ${actor.perceivedState}
            Strongest connection: ${
              actor.social.connections.maxByOrNull { it.value }?.let { "${it.key}: ${it.value.display()}" } ?: "-"
            }
            Connection sum: ${
              actor.social.connections.entries.sumOf { it.value }.display()
            } (ideal: ${actor.preferences.minConnectionStrengthSum.display()})
            ${if (actor.social.children.isNotEmpty()) "${actor.social.children.count()} Children with ages: ${actor.social.children.map { it.age.display() }}" else ""}
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

          OutlinedButton(onClick = { speed.value = speed.value.next }, modifier = Modifier.padding(end = 16.dp)) {
            when (speed.value) {
              Speed.NORMAL -> Text("1x")
              Speed.DOUBLE -> Text("2x")
              Speed.QUADRUPLE -> Text("4x")
            }
          }

          Spacer(Modifier.weight(1f))

          if (lastTimesSimulationTookTooLong.value
              .takeIf { it.count() == lastTimesSimulationTookTooLongSize }
              ?.all { it.elapsedNow().inWholeSeconds < 2 } == true
          ) {
            // We skipped at least `lastTimesSimulationTookTooLongSize` ticks within the last 2 seconds
            Text(
              "${game.worldState} - ${tick.value} ticks - " +
                  "Too fast, please run the simulation slower.",
              style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
            )
          } else {
            Text(
              "${game.worldState} - ${tick.value} ticks - Simulation duration per ${speed.value.factor} ${if (speed.value.factor == 1) "tick" else "ticks"}: " +
                  "${lastTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0).padStart(5, '0')} - " +
                  "max ${maxTickDuration.value.toString(DurationUnit.MILLISECONDS, decimals = 0).padStart(5, '0')}",
              style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
            )
          }

          Spacer(Modifier.weight(2f))
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

enum class Speed {
  NORMAL, DOUBLE, QUADRUPLE;

  val next: Speed
    get() = when (this) {
      NORMAL -> DOUBLE
      DOUBLE -> QUADRUPLE
      QUADRUPLE -> NORMAL
    }

  val factor: Int
    get() = when (this) {
      NORMAL -> 1
      DOUBLE -> 2
      QUADRUPLE -> 4
    }
}