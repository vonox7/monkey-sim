package definitions

class Position(
  val x: Int,
  val y: Int,
) {
  // Add
  operator fun plus(other: Position): Position {
    return Position(x + other.x, y + other.y)
  }

  operator fun minus(other: Position): Position {
    return Position(x - other.x, y - other.y)
  }
}
