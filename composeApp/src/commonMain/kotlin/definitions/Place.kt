package definitions

import androidx.compose.ui.graphics.Color

sealed class Place(
  val position: Position,
  val zIndex: Int,
  val color: Color,
) {
  override fun toString(): String {
    return "${this::class.simpleName} at $position"
  }
}

class Work(position: Position, val maxPeople: Int) : Place(position, zIndex = 1, color = Color(0xFF4e4553))

class FoodShop(position: Position) : Place(position, zIndex = 2, Color(0xFF359750))

class Home(position: Position) : Place(position, zIndex = 3, color = Color(0xFF94bdf8))
// Also other shop?

// Also education?