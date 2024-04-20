package definitions

import display
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Position(
  val x: Double,
  val y: Double,
) {
  // Add
  operator fun plus(other: Position): Position {
    return Position(x + other.x, y + other.y)
  }

  operator fun minus(other: Position): Position {
    return Position(x - other.x, y - other.y)
  }

  fun directionTo(position: Position, maxTravelSpeed: Double): Direction {
    val dx = position.x - x
    val dy = position.y - y

    // Normalize to maxTravelSpeed
    val distance = sqrt(dx * dx + dy * dy)
    if (distance > maxTravelSpeed) {
      val factor = maxTravelSpeed / distance
      return Direction(dx * factor, dy * factor)
    } else {
      return Direction(dx, dy)
    }
  }

  fun distanceTo(position: Position): Double {
    val dx = position.x - x
    val dy = position.y - y

    return sqrt(dx * dx + dy * dy)
  }

  override fun equals(other: Any?): Boolean {
    if (other !is Position) return false
    return other.x.roundToInt() == x.roundToInt() && other.y.roundToInt() == y.roundToInt()
  }

  override fun hashCode(): Int {
    return x.roundToInt() * 3131 + y.roundToInt()
  }

  override fun toString(): String {
    return "(x=${x.display()}, y=${y.display()})"
  }
}


data class Direction(val dx: Double, val dy: Double)