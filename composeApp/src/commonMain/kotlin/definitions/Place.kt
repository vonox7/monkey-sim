package definitions

sealed class Place(
  val position: Position,
)

class Home(position: Position) : Place(position)

class Work(position: Position, val maxPeople: Int) : Place(position)

class FoodShop(position: Position) : Place(position)
// Also other shop?
