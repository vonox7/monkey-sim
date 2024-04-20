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
import definitions.WorkingCategory
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
fun OccupationChart(history: History) {
  val lastEntry = history.entries.last()

  Box(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 8.dp)) {
    Text("Occupation")
  }
  val workingHistory = history.entries
      .map { entry -> entry.workingInfo.values.map { it / entry.worldPopulation.toFloat() }.toList() }
  val transposedWorkingHistory = workingHistory[0].indices.map { i -> workingHistory.map { it[i] } }
  val workingInfoData: StackedAreaPlotDataAdapter<Float> = StackedAreaPlotDataAdapter(
    history.entries.map { it.time.toFloat() },
    transposedWorkingHistory
  )
  val workingInfoStyle = history.entries.first().workingInfo.keys.map { category ->
    StackedAreaStyle(
      LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
      AreaStyle(SolidColor(WorkingCategory.colors[category]!!))
    )
  }

  Box(Modifier.height(100.dp).padding(end = 8.dp)) {
    XYGraph(
      rememberLinearAxisModel(
        history.entries.first().time.toFloat()..lastEntry.time.toFloat() + 0.001f
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
        workingInfoData,
        workingInfoStyle,
        AreaBaseline.ConstantLine(0f),
      )
    }
  }

  val workingList = lastEntry.workingInfo.entries.reversed()
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
    workingList.windowed(
      size = (workingList.size + 2) / 3,
      step = (workingList.size + 2) / 3,
      partialWindows = true
    ).forEach { categories ->
      Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
        categories.forEach { (category, count) ->
          Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(Modifier.size(16.dp).clip(CircleShape).background(WorkingCategory.colors[category]!!))
            Spacer(Modifier.width(8.dp))
            Text(
              category.name,
              style = LocalTextStyle.current.copy(fontSize = 14.sp),
            )
            Spacer(Modifier.width(8.dp))
            Spacer(Modifier.weight(1f))
            Text(
              (100 * count.toDouble() / lastEntry.worldPopulation.toDouble()).roundToInt().toString() + "%",
              style = LocalTextStyle.current.copy(fontSize = 14.sp, fontFeatureSettings = "tnum"),
            )
          }
        }
      }
    }
  }
}