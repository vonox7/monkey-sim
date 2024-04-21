package game

import definitions.Actor
import definitions.WorkingCategory
import definitions.World
import definitions.WorldState
import kotlin.reflect.KClass

class History {
  class Entry(
    val stateToPeopleCount: Map<KClass<out Actor.State>, Int>,
    val worldPopulation: Int,
    val time: Double,
    val peopleWithPartner: Int,
    val workingInfo: Map<WorkingCategory, Int>,
  )

  private val _entries: MutableList<Entry> = mutableListOf()
  val entries get(): List<Entry> = _entries

  fun add(world: World, worldState: WorldState) {

    val currentStates: Map<KClass<out Actor.State>, List<Actor>> =
      world.actors.groupBy { actor -> actor.perceivedState::class }

    val currentWorkingCategory = world.actors.groupBy { it.workingCategory }

    _entries.add(
      Entry(
        Actor.State.allStates.associateWith { currentStates[it]?.size ?: 0 },
        world.actors.size,
        worldState.timestamp,
        world.actors.count { it.social.partner != null },
        workingInfo = WorkingCategory.entries.associateWith { currentWorkingCategory[it]?.size ?: 0 }
      )
    )

    // We only keep the last 24 hours
    _entries.removeAll { it.time < worldState.timestamp - 24 }
  }
}