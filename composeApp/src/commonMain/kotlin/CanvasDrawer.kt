import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
        fun Position.toOffset(): Offset {
          return Offset(
            x.toFloat() / worldWidth * scope.size.width,
            y.toFloat() / worldHeight * scope.size.height
          )
        }

        // Background
        val worldBottomRight = Position(worldWidth.toDouble(), worldHeight.toDouble()).toOffset()
        drawRect(
          color = Color(0xFFFFFFFF),
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y)
        )
        // Border
        drawRect(
          color = Color(0xFF303030),
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y),
          style = Stroke(1.dp.toPx())
        )

        // Draw places
        world.places
            .sortedBy { it.zIndex }
            .forEach { place ->
              val position = place.position
              val topLeft = position.toOffset()
              val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
              val outline = 1.dp.toPx()
              val size = 7.dp.toPx()
              val resizedTopLeft = center - Offset(size, size) * 0.5f

              when (place) {
                is Home -> {
                  // Draw box
                  drawRect(
                    color = Color.White,
                    topLeft = resizedTopLeft - Offset(outline, outline),
                    size = Size(size + outline * 2, size + outline * 2),
                  )
                  drawRect(
                    color = place.color,
                    topLeft = resizedTopLeft,
                    size = Size(size, size),
                  )

                  // Draw house roof
                  val outlinePath = Path()
                  outlinePath.moveTo(resizedTopLeft.x - outline, resizedTopLeft.y)
                  outlinePath.lineTo(resizedTopLeft.x + size / 2, resizedTopLeft.y - size / 2 - outline)
                  outlinePath.lineTo(resizedTopLeft.x + size + outline, resizedTopLeft.y)
                  outlinePath.close()
                  drawPath(outlinePath, color = Color.White)

                  val path = Path()
                  path.moveTo(resizedTopLeft.x, resizedTopLeft.y)
                  path.lineTo(resizedTopLeft.x + size / 2, resizedTopLeft.y - size / 2)
                  path.lineTo(resizedTopLeft.x + size, resizedTopLeft.y)
                  path.close()
                  drawPath(path, color = place.color)
                }

                is FoodShop -> {
                  // Draw box with left side shorter
                  drawRect(
                    color = Color.White,
                    topLeft = resizedTopLeft - Offset(outline, outline),
                    size = Size(size + outline * 2, size + outline * 2),
                  )
                  val path = Path()
                  path.moveTo(resizedTopLeft.x, resizedTopLeft.y + size * 0.2f)
                  path.lineTo(resizedTopLeft.x + size, resizedTopLeft.y)
                  path.lineTo(resizedTopLeft.x + size, resizedTopLeft.y + size)
                  path.lineTo(resizedTopLeft.x, resizedTopLeft.y + size)
                  path.close()

                  drawPath(path, color = place.color)
                }

                is Work -> {
                  // Draw box
                  drawRect(
                    color = Color.White,
                    topLeft = resizedTopLeft - Offset(outline, outline),
                    size = Size(size + outline * 2, size + outline * 2),
                  )
                  drawRect(
                    color = place.color,
                    topLeft = resizedTopLeft,
                    size = Size(size, size),
                  )

                  // Draw chimney
                  val path = Path()
                  path.moveTo(resizedTopLeft.x + size, resizedTopLeft.y)
                  path.lineTo(resizedTopLeft.x + size, resizedTopLeft.y - size / 2)
                  path.lineTo(resizedTopLeft.x + size * 0.7f, resizedTopLeft.y - size / 2)
                  path.lineTo(resizedTopLeft.x + size * 0.7f, resizedTopLeft.y)
                  path.close()
                  drawPath(path, color = place.color)
                }
              }
            }

        // Draw actors

        world.actors.forEach { actor ->
          val topLeft = actor.currentPosition.toOffset()
          val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
          val size = 2.4f
          drawCircle(
            color = Color(0xFFceb28b),
            center = center,
            radius = size.dp.toPx(),
          )
          drawCircle(
            color = Color(0xFF4a2a0b),
            center = center,
            radius = (size - 0.6f).dp.toPx(),
          )
        }

        // Draw social connections
        world.actors.forEach { actor ->
          actor.socialConnections.connections.forEach { (otherActor, strength) ->
            val position = actor.currentPosition
            val topLeft = position.toOffset()

            val otherPosition = otherActor.currentPosition
            val otherTopLeft = Position(otherPosition.x, otherPosition.y)
            drawLine(
              color = Color.Black,
              start = Offset(topLeft.x + size.width / 2, topLeft.y + size.height / 2),
              end = Offset((otherTopLeft.x + size.width / 2).toFloat(), (otherTopLeft.y + size.height / 2).toFloat()),
              strokeWidth = 2f,
            )
          }
        }
      }
    }
  }
}