import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.dp
import definitions.*

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
        val scope = this@inset
        fun Position.toPx(): Offset {
          return Offset(
            x.toFloat() / worldWidth * scope.size.width,
            y.toFloat() / worldHeight * scope.size.height
          )
        }

        // Background
        val worldBottomRight = Position(worldWidth, worldHeight).toPx()
        drawRect(
          color = Color(0xFFCACACA),
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y)
        )

        // Draw places
        world.places.forEach { place ->
          val position = place.position
          val topLeft = position.toPx()
          val size = 3.dp.toPx()
          drawRect(
            color = when (place) {
              is Home -> Color.Blue
              is FoodShop -> Color.Green
              is Work -> Color.Yellow
            },
            topLeft = topLeft - Offset(size, size),
            size = Size(size, size) * 2f,
          )
        }

        // Draw actors
        world.actors.forEach { actor ->
          val topLeft = actor.currentPosition.toPx()
          val radius = 1.5f.dp.toPx()
          drawCircle(
            color = Color.Red,
            center = Offset(topLeft.x + radius, topLeft.y + radius),
            radius = radius,
          )
        }

        world.actors.forEach { actor ->
          actor.socialConnections.connections.forEach { (otherActor, strength) ->
            val position = actor.currentPosition
            val topLeft = position.toPx()

            val otherPosition = otherActor.currentPosition
            val otherTopLeft = Position(otherPosition.x, otherPosition.y)
            drawLine(
              color = Color.Black,
              start = Offset(topLeft.x + size.width / 2, topLeft.y + size.height / 2),
              end = Offset(otherTopLeft.x + size.width / 2, otherTopLeft.y + size.height / 2),
              strokeWidth = 2f,
            )
          }
        }
      }
    }
  }
}