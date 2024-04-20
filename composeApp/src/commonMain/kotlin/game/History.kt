package game

import definitions.Actor
import definitions.World
import definitions.WorldState
import kotlin.reflect.KClass

class History {
  class Entry(
    val stateToPeopleCount: Map<KClass<out Actor.State>, Int>,
    val worldPopulation: Int,
    val time: Double,
  )

  private val _entries: MutableList<Entry> = mutableListOf()
  val entries get(): List<Entry> = _entries

  fun add(world: World, worldState: WorldState) {

    val currentStates: Map<KClass<out Actor.State>, List<Actor>> =
      world.actors.groupBy { actor -> actor.perceivedState::class }

    _entries.add(
      Entry(
        Actor.State.allStates.associateWith { currentStates[it]?.size ?: 0 },
        world.actors.size,
        worldState.timestamp,
      )
    )

    // We only keep the last 24 hours
    _entries.removeAll { it.time < worldState.timestamp - 24 }
  }
}