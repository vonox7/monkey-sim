package ui.charts.occupation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import definitions.Actor
import game.History
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.StackedAreaPlot
import io.github.koalaplot.core.line.StackedAreaPlotDataAdapter
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberLinearAxisModel
import kotlin.math.roundToInt

@Composable
fun ActorStatesChart(history: History) {
  val historyEntries = history.tickEntries
  val lastEntry = historyEntries.last()
  Box(modifier = Modifier.fillMaxWidth().padding(start = 6.dp, bottom = 6.dp)) {
    Text("State distribution")
  }
  val gameHistory = historyEntries
      .map { entry -> entry.stateToPeopleCount.values.map { it / entry.worldPopulation.toFloat() }.toList() }
  val transposedGameHistory = gameHistory[0].indices.map { i -> gameHistory.map { it[i] } }

  val stackData: StackedAreaPlotDataAdapter<Float> = StackedAreaPlotDataAdapter(
    historyEntries.map { it.timestamp.toFloat() },
    transposedGameHistory
  )
  val stateHistoryStyle = historyEntries.first().stateToPeopleCount.keys.map { stateClass ->
    StackedAreaStyle(
      LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
      AreaStyle(SolidColor(Actor.State.colors[stateClass]!!))
    )
  }

  Box(Modifier.height(200.dp).padding(end = 8.dp)) {
    XYGraph(
      rememberLinearAxisModel(
        historyEntries.first().timestamp.toFloat()..lastEntry.timestamp.toFloat() + 0.001f
      ),
      rememberLinearAxisModel(0f..1.0f),
      xAxisTitle = { },
      yAxisTitle = { },
      xAxisLabels = { timestamp ->
        Text(
          (timestamp.toDouble() % 24).roundToInt().toString() + "h",
          color = MaterialTheme.colors.onBackground,
          style = MaterialTheme.typography.body2.copy(fontSize = 12.sp, lineHeight = 12.sp),
        )
      },
      yAxisLabels = { }
    ) {
      StackedAreaPlot(
        stackData,
        stateHistoryStyle,
        AreaBaseline.ConstantLine(0f),
      )
    }
  }

  val statesList = lastEntry.stateToPeopleCount.entries.reversed()
  Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
    statesList.windowed(
      size = (statesList.size + 3) / 4,
      step = (statesList.size + 3) / 4,
      partialWindows = true
    ).forEach { subList ->
      Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
        subList.forEach { (clazz, count) ->
          val color = Actor.State.colors[clazz]!!
          Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(6.dp))
            Text(
              clazz.simpleName!!,
              style = LocalTextStyle.current.copy(fontSize = 12.sp, lineHeight = 12.sp),
            )
            Spacer(Modifier.width(4.dp))
            Spacer(Modifier.weight(1f))
            Text(
              (100 * count.toDouble() / lastEntry.worldPopulation.toDouble()).roundToInt().toString() + "%",
              maxLines = 1,
              style = LocalTextStyle.current.copy(fontSize = 12.sp, lineHeight = 12.sp, fontFeatureSettings = "tnum"),
            )
          }
          Spacer(Modifier.height(6.dp))
        }
      }
    }
  }
}