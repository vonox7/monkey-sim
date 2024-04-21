import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import definitions.*
import game.Game
import kotlin.math.sqrt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawWorldOnCanvas(
  inspectingActor: Actor,
  game: Game,
  worldWidth: Int,
  worldHeight: Int,
) {
  val mousePosition: MutableState<Offset?> = remember { mutableStateOf(null) }
  val textMeasurer = rememberTextMeasurer()
  val textStyle = LocalTextStyle.current
  val world = game.world

  Canvas(modifier = Modifier.fillMaxSize().onPointerEvent(PointerEventType.Move) {
    val position = it.changes.first().position
    mousePosition.value = position
  }.onPointerEvent(PointerEventType.Exit) {
    mousePosition.value = null
  }) {
    inset(
      5.dp.toPx(),
    ) {
      val scope = this@inset
      fun Position.toOffset(): Offset {
        return Offset(
          (x / worldWidth * scope.size.width).toFloat(),
          (y / worldHeight * scope.size.height).toFloat()
        )
      }

      val worldBottomRight = Position(worldWidth.toDouble(), worldHeight.toDouble()).toOffset()

      fun drawBackground() {
        // Daytime: brightness 1, nighttime: 0.2, dawn/dusk: inter
        val overlayStrength = when (val hour = game.worldState.hour) {
          in 6.0..7.0 -> 7 - hour
          in 18.0..19.0 -> hour - 18
          in 7.0..18.0 -> 0.0
          else -> 1.0
        }


        drawRect(
          color = Color(0xFFFFFFFF - (0xBD * (overlayStrength)).toLong() * 0x10101),
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
          .forEach { (_, places) ->
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
                    color = place.color,
                    topLeft = resizedTopLeft - Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = Size(size + 6.dp.toPx(), size + 6.dp.toPx()),
                  )
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
          .groupBy { it.currentPosition }
          .values
          .forEach { actorsOnSameSpot ->
            val groupAxisSize = sqrt(actorsOnSameSpot.size.toDouble()).toInt()

            actorsOnSameSpot.forEachIndexed { index, actor ->
              if (actor == inspectingActor) return@forEachIndexed

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

        run {
          val topLeft = inspectingActor.currentPosition.toOffset()
          val center = topLeft + Position(0.5, 0.5).toOffset()
          val outline = 2f
          val size = 4f
          drawCircle(
            color = Color(0xFFfb4332),
            center = center,
            radius = (size + outline).dp.toPx(),
          )
          drawCircle(
            color = Color(0xFFFFFFFF),
            center = center,
            radius = size.dp.toPx(),
          )
        }
      }

      fun drawSocialConnections(world: World) {
        val connectionStrengths = mutableListOf<Double>()
        world.actors.forEach { actor ->
          actor.social.connections.forEach { (otherActor, strength) ->
            if (actor.lovePotential(otherActor)) {
              connectionStrengths.add(strength)
            }
          }
        }
        connectionStrengths.sortDescending()
        val minDisplayConnectionStrength = connectionStrengths.getOrNull(30) ?: 0.0

        world.actors.forEach { actor ->
          actor.social.connections.forEach connectionLoop@{ (otherActor, strength) ->
            if (strength < minDisplayConnectionStrength || !actor.lovePotential(otherActor)) return@connectionLoop
            val position = actor.currentPosition
            val topLeft = position.toOffset()

            val otherPosition = otherActor.currentPosition
            val otherTopLeft = otherPosition.toOffset()
            drawLine(
              color = if (strength > 8.0) Color(0x33FF3333) else Color(0x33000000),
              start = topLeft,
              end = otherTopLeft,
              strokeWidth = strength.toFloat(),
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

        val color: Long = (0x60 * overlayStrength).toLong() * 0x1000000
        drawRect(
          color = Color(color),
          topLeft = Offset(0f, 0f),
          size = Size(worldBottomRight.x, worldBottomRight.y)
        )

        // Draw light if it's nighttime and a person is here
        if (game.worldState.hour !in 7.0..18.0) {

          val lightPositionsDrawn = mutableSetOf<Position>()

          world.actors
            .forEach { actor ->
              if (actor.perceivedState !is Actor.State.DurationalState.Sleeping &&
                actor.targetState == actor.perceivedState && // Not commuting
                actor.currentPosition !in lightPositionsDrawn
              ) {
                lightPositionsDrawn.add(actor.currentPosition)

                val topLeft = actor.currentPosition.toOffset()
                val center = topLeft + (Position(1.0, 1.0).toOffset() * 0.5f)
                drawCircle(
                  color = Color(0x61f0D957),
                  center = center,
                  radius = 8.dp.toPx(),
                )
              }
            }
        }
      }

      fun drawPlaceTooltip() {
        if (mousePosition.value != null) {
          // Move by 12px to top left corner so we can lookup the places by topLeft position and not by center drawn position
          val mouseWorldPosition = Position(
            (mousePosition.value!!.x - 12).toDouble() / size.width * worldWidth,
            (mousePosition.value!!.y - 12).toDouble() / size.height * worldHeight
          )

          val place = world.places.values.flatten().minByOrNull {
            it.position.distanceTo(mouseWorldPosition)
          }

          if (place != null && place.position.distanceTo(mouseWorldPosition) < 10) {
            val text = StringBuilder(place.toString())
            if (place !is Home && place.openHours != 0..0) {
              text.append("\nOpen hours: ${place.openHours}")
            }
            place.work?.let { work ->
              text.append("\nWork: $work")
              text.append("\nWorking hours: ${work.workableHours}")
              if (work.coreWorkingHours != work.workableHours) {
                text.append(" (core: ${work.coreWorkingHours})")
              }
            }

            drawText(
              textMeasurer,
              text.toString(),
              style = textStyle.copy(fontSize = 10.sp, lineHeight = 12.sp, background = Color(0xDDDDDDDD)),
              topLeft = place.position.toOffset() + Offset(16.0f, 8.0f),
              padding = 10.0f,
              borderColor = Color(0xFF000000),
            )
          }
        }
      }


      drawBackground()

      drawPlaces()

      drawActors()

      drawSocialConnections(world)

      drawNighttimeOverlay()

      drawPlaceTooltip()
    }
  }
}