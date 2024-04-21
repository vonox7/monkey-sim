import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

fun Double.display(decimals: Int = 2): String {
  require(decimals != 0) { "Use Math round to get an Int" }
  val factor = 10.toDouble().pow(decimals.toDouble())
  return ((this * factor).roundToLong().toDouble() / factor).toString()
}

// Draws a text with a padding (including background color). Implementation extracted from compose drawText()
fun DrawScope.drawText(
  textMeasurer: TextMeasurer,
  text: String,
  topLeft: Offset = Offset.Zero,
  style: TextStyle = TextStyle.Default,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  size: Size = Size.Unspecified,
  blendMode: BlendMode = DrawScope.DefaultBlendMode,
  padding: Float,
  borderColor: Color? = null,
  borderStroke: Stroke = Stroke(1.dp.toPx()),
) {
  val textLayoutResult = textMeasurer.measure(
    text = AnnotatedString(text),
    style = style.copy(background = Color.Transparent), // We paint the background ourselves via the border, so don't paint it twice
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
    constraints = Constraints(
      0,
      ceil(this.size.width - topLeft.x).roundToInt(),
      0,
      ceil(this.size.height - topLeft.y).roundToInt()
    ),
    layoutDirection = layoutDirection,
    density = this
  )

  // Here we draw now the padding with a background color
  drawRect(
    color = style.background,
    topLeft = topLeft,
    size = Size(
      textLayoutResult.size.width.toFloat() + 2 * padding,
      textLayoutResult.size.height.toFloat() + 2 * padding
    ),
  )
  if (borderColor != null) {
    drawRect(
      color = borderColor,
      topLeft = topLeft,
      size = Size(
        textLayoutResult.size.width.toFloat() + 2 * padding,
        textLayoutResult.size.height.toFloat() + 2 * padding
      ),
      style = borderStroke
    )
  }

  withTransform({
    translate(topLeft.x + padding, topLeft.y + padding)

    // Clip
    if (textLayoutResult.hasVisualOverflow &&
      textLayoutResult.layoutInput.overflow != TextOverflow.Visible
    ) {
      clipRect(
        left = 0f,
        top = 0f,
        right = textLayoutResult.size.width.toFloat(),
        bottom = textLayoutResult.size.height.toFloat()
      )
    }
  }) {
    textLayoutResult.multiParagraph.paint(
      canvas = drawContext.canvas,
      blendMode = blendMode
    )
  }
}