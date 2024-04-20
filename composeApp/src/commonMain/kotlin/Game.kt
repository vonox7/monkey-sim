import definitions.Actor
import definitions.World
import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz
import kotlin.reflect.KClass

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(timestamp = 0.0, hour = 6.9, day = 0)
  val history = History().apply { add(world, worldState) }

  fun tick(elapsedHours: Double) {
    worldState.tick(elapsedHours)
    world.actors.forEach { it.tick(world, worldState, elapsedHours) }
    history.add(world, worldState)
  }
}

class History {
  class Entry(
    val stateToPeopleCount: Map<KClass<out Actor.State>, Int>,
    val worldPopulation: Int,
    val time: Double,
  )

  private val entries: MutableList<Entry> = mutableListOf()
  fun get(): List<Entry> = entries

  fun add(world: World, worldState: WorldState) {

    val currentStates: Map<KClass<out Actor.State>, List<Actor>> =
      world.actors.groupBy { actor -> actor.perceivedState::class }

    entries.add(
      Entry(
        Actor.State.allStates.associateWith { currentStates[it]?.size ?: 0 },
        world.actors.size,
        worldState.timestamp,
      )
    )

    // We only keep the last 24 hours
    entries.removeAll { it.time < worldState.timestamp - 24 }
  }
}
