package definitions

open class World(
  val width: Int,
  val height: Int,
  val places: List<Place>,
  val actors: List<Actor>,
)