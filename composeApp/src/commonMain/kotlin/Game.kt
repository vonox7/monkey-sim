import definitions.World
import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(time = 6.9, day = 0)
  val history = History()

  fun tick(elapsedHours: Double) {
    worldState.tick(elapsedHours)
    world.actors.forEach { it.tick(world, worldState, elapsedHours) }
    history.add(world)
  }
}

class History {
  class Entry(
    val states: Map<String, Int>,
    val numberOfPeople: Int,
  )

  private val entries: MutableList<Entry> = mutableListOf()
  fun get(): List<Entry> = entries

  fun add(world: World) {
    val newStates: Map<String, Int> = world.actors
        .groupBy { actor -> actor.perceivedState::class.simpleName!! }
        .toList()
        .associate { (state, actors) -> state to actors.count() }

    entries.add(
      Entry(
        newStates,
        world.actors.size
      )
    )
  }
}
