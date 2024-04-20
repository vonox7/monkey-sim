import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.dp
import definitions.World

class CanvasDrawer(
  private val canvas: DrawScope,
  private val worldWidth: Int,
  private val worldHeight: Int,
) {
  fun draw(world: World) {
    // Draw the world
    with(canvas) {
      inset(
        5.dp.toPx(),
      ) {
        val worldBottomRight = positionToPx(worldWidth, worldHeight)
        drawRect(
          color = Color.Gray,
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y)
        )

        // Draw places
        world.places.forEach { place ->
          val position = place.position
          val topLeft = positionToPx(position.x, position.y)
          val size = positionToPx(position.x + 1, position.y + 1) - topLeft
          drawRect(
            color = Color.Blue,
            topLeft = topLeft,
            size = Size(size.x, size.y),
          )
        }
      }
    }
  }

  private fun DrawScope.positionToPx(x: Int, y: Int): Offset {
    return Offset(
      x.toFloat() / worldWidth * size.width,
      y.toFloat() / worldHeight * size.height
    )
  }
}