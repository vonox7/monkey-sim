package definitions

import game.Settings
import kotlin.reflect.KClass

open class World(
  val width: Int,
  val height: Int,
  val places: Map<KClass<out Place>, List<Place>>,
  val actors: MutableList<Actor>,
) {
  val log = StringBuilder()
  fun log(string: String) {
    log.appendLine(string) // TODO Make log viewable in UI
  }

  val settings = Settings()

  // Needs to be manually synchronized with Actor.currentPosition
  val actorsGroupedByPosition: MutableMap<Position, MutableList<Actor>> = actors
    .groupingBy { it.currentPosition }
    .aggregateTo(mutableMapOf()) { _, accumulator, element, _ ->
      (accumulator ?: mutableListOf()).apply { add(element) }
    }
}