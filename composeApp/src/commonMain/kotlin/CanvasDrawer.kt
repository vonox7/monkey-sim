import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import definitions.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun DrawWorldOnCanvas(
  game: Game,
  worldWidth: Int,
  worldHeight: Int,
) {
  val textMeasurer = rememberTextMeasurer()
  val textStyle = LocalTextStyle.current
  val world = game.world

  Canvas(modifier = Modifier.fillMaxSize()) {
    inset(
      5.dp.toPx(),
    ) {
      val scope = this@inset
      fun Position.toOffset(): Offset {
        return Offset(
          x.roundToInt().toFloat() / worldWidth * scope.size.width,
          y.roundToInt().toFloat() / worldHeight * scope.size.height
        )
      }

      val worldBottomRight = Position(worldWidth.toDouble(), worldHeight.toDouble()).toOffset()

      fun drawBackground() {
        // Daytime: brightness 1, nighttime: 0.2, dawn/dusk: inter
        val brightness = when (val hour = game.worldState.hour) {
          in 6.0..7.0 -> 0.2 + (hour - 6) * 0.8
          in 18.0..19.0 -> 0.2 + (19 - hour) * 0.8
          in 7.0..18.0 -> 1.0
          else -> 0.2
        }

        val color: Long = 0xFF000000 + (0xFF * brightness).toLong() * 0x010101
        drawRect(
          color = Color(color),
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
      }

      fun drawText(text: String, topLeft: Offset, size: Float) = drawText(
        textMeasurer,
        text,
        style = textStyle.copy(fontSize = 5.dp.toSp(), lineHeight = 7.dp.toSp(), textAlign = TextAlign.Center),
        topLeft = topLeft,
        size = Size(size, size)
      )

      fun drawPlaces() {
        world.places
          .entries
          .sortedBy { it.value.first().zIndex }
          .fastForEach { (_, places) ->
            places.forEach { place ->
              val position = place.position
              val topLeft = position.toOffset()
              val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
              val outline = 1.dp.toPx()
              val size = 7.dp.toPx()
              val resizedTopLeft = center - Offset(size, size) * 0.5f

              fun DrawScope.drawBoxWithLeftSideShort() {
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

              when (place) {
                is Home -> {
                  drawRect(
                    color = Color.White,
                    topLeft = resizedTopLeft - Offset(outline, 0f),
                    size = Size(size + outline * 2, size + outline),
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
                  drawBoxWithLeftSideShort()
                }

                is Industry -> {
                  drawBoxWithLeftSideShort()

                  // Draw chimney
                  val chimneyPath = Path()
                  chimneyPath.moveTo(resizedTopLeft.x + size, resizedTopLeft.y + size * 0.1f)
                  chimneyPath.relativeLineTo(0f, -size * 0.5f)
                  chimneyPath.relativeLineTo(-size * 0.3f, 0f)
                  chimneyPath.relativeLineTo(0f, size * 0.5f)
                  chimneyPath.close()
                  drawPath(chimneyPath, color = place.color)
                }

                is Park -> {
                  drawRect(
                    color = Color.White,
                    topLeft = resizedTopLeft - Offset(outline, outline),
                    size = Size(size + outline * 2, size + outline * 2),
                  )

                  // Draw tree
                  //TODO test, right now there are no parks
                  val treePath = Path()
                  treePath.moveTo(resizedTopLeft.x + size / 2, resizedTopLeft.y)
                  treePath.relativeLineTo(0f, -size * 0.5f)
                  treePath.relativeLineTo(-size * 0.2f, size * 0.2f)
                  treePath.relativeLineTo(size * 0.4f, 0f)
                  treePath.relativeLineTo(-size * 0.2f, size * 0.2f)
                  treePath.close()
                  drawPath(treePath, color = place.color)
                }

                is Gym -> {
                  drawCircle(
                    color = Color.White,
                    center = center,
                    radius = size / 2 + outline,
                  )

                  drawText("🏋️", topLeft = resizedTopLeft, size = size)
                }

                is University -> {
                  drawCircle(
                    color = Color.White,
                    center = center,
                    radius = size / 2 + outline,
                  )

                  drawText("🎓", topLeft = resizedTopLeft, size = size)
                }

                is Club -> {
                  drawCircle(
                    color = Color.White,
                    center = center,
                    radius = size / 2 + outline,
                  )

                  drawText("🎉", topLeft = resizedTopLeft, size = size)
                }
              }
            }
          }
      }

      fun drawActors() {

        world.actors
          .groupBy { it.currentPosition.x.roundToInt() to it.currentPosition.y.roundToInt() }
          .values
          .forEach { actorsOnSameSpot ->
            val groupAxisSize = sqrt(actorsOnSameSpot.size.toDouble()).toInt()

            actorsOnSameSpot.forEachIndexed { index, actor ->
              val reverseIndex = actorsOnSameSpot.size - index - 1
              var position = actor.currentPosition
              position += Position(
                (reverseIndex % groupAxisSize).toDouble() + reverseIndex * 0.1,
                (reverseIndex / groupAxisSize).toDouble() * 1.1
              )
              val topLeft = position.toOffset()
              val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
              val outline = 1f
              val size = 2.2f
              drawCircle(
                color = Color(0xFFceb28b),
                center = center,
                radius = (size + outline).dp.toPx(),
              )
              drawCircle(
                color = Color(0xFF4a2a0b),
                center = center,
                radius = size.dp.toPx(),
              )
            }
          }
      }

      fun drawSocialConnections(world: World) {
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

      fun drawNighttimeOverlay() {
        val overlayStrength = when (val hour = game.worldState.hour) {
          in 6.0..7.0 -> 7 - hour
          in 18.0..19.0 -> hour - 18
          in 7.0..18.0 -> 0.0
          else -> 1.0
        }

        val color: Long = (0x98 * overlayStrength).toLong() * 0x1000000
        drawRect(
          color = Color(color),
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y)
        )

        world.places
          .values
          .flatten()
          .forEach { place ->
            // Draw light if it's nighttime and a person is here
            if (game.worldState.hour !in 7.0..18.0 &&
              world.actors.any { it.currentPosition == place.position && it.perceivedState !is Actor.State.DurationalState.Sleeping }
            ) {
              val position = place.position
              val topLeft = position.toOffset()
              val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
              drawCircle(
                color = Color(0x61f0D957),
                center = center,
                radius = 8.dp.toPx(),
              )
            }
          }
      }


      drawBackground()

      drawPlaces()

      drawActors()

      drawSocialConnections(world)

      drawNighttimeOverlay()
    }
  }
}