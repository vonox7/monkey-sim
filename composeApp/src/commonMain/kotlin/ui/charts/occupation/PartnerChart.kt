package ui.charts.occupation

import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import game.History
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberLinearAxisModel
import kotlin.math.roundToInt

@Composable
fun PartnerChart(history: History) {
  val lastEntry = history.entries.last()

  val partnerPercentageData = history.entries.map { entry ->
    Point(
      entry.time.toFloat(),
      100f * entry.peopleWithPartner.toFloat() / entry.worldPopulation.toFloat()
    )
  }
  Row(modifier = Modifier.fillMaxWidth().padding(start = 6.dp, bottom = 4.dp)) {
    Text("People with partner")
    Spacer(Modifier.weight(1f))
    Text(
      "${lastEntry.peopleWithPartner} / ${lastEntry.worldPopulation} (${(100 * lastEntry.peopleWithPartner.toDouble() / lastEntry.worldPopulation.toDouble()).roundToInt()}%)",
      style = LocalTextStyle.current.copy(fontSize = 14.sp, fontFeatureSettings = "tnum"),
    )
  }
  Box(Modifier.height(100.dp).padding(end = 8.dp)) {
    XYGraph(
      rememberLinearAxisModel(
        history.entries.first().time.toFloat()..lastEntry.time.toFloat() + 0.001f
      ),
      rememberLinearAxisModel(0f..100.0f),
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
      LinePlot(
        partnerPercentageData,
        lineStyle = LineStyle(SolidColor(Color.Black), strokeWidth = 1.5.dp),
      )
    }
  }
}