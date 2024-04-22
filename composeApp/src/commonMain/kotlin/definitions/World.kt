package definitions

import game.Settings
import kotlin.reflect.KClass

open class World(
  val width: Int,
  val height: Int,
  val places: Map<KClass<out Place>, List<Place>>,
  val actors: MutableList<Actor>,
) {
  val settings = Settings()
}