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
    val timestamp: Double,
    val peopleWithPartner: Int,
    val workingInfo: Map<WorkingCategory, Int>,
  )

  // TickEntries are stored for the last 24 hours, but one per simulation tick
  private val _tickEntries: MutableList<Entry> = mutableListOf()
  val tickEntries get(): List<Entry> = _tickEntries

  // Long term entries are stored every few minutes, but infinite (so memory grows here linearly)
  private val _longTermEntries: MutableList<Entry> = mutableListOf()
  val longTermEntries get(): List<Entry> = _longTermEntries

  fun add(world: World, worldState: WorldState) {

    val currentStates: Map<KClass<out Actor.State>, Int> =
      world.actors.groupingBy { actor -> actor.perceivedState::class }.eachCount()

    val currentWorkingCategory = world.actors.groupingBy { it.workingCategory }.eachCount()

    val newEntry = Entry(
      Actor.State.allStates.associateWith { currentStates[it] ?: 0 },
      world.actors.size,
      worldState.timestamp,
      world.actors.count { it.social.partner != null },
      workingInfo = WorkingCategory.entries.associateWith { currentWorkingCategory[it] ?: 0 }
    )
    _tickEntries.add(newEntry)

    // We only keep the last 24 hours for the tick entries
    _tickEntries.removeAll { it.timestamp < worldState.timestamp - 24 }

    // Add an entry every few minutes to the long term entries
    if (_longTermEntries.isEmpty() || _longTermEntries.last().timestamp < worldState.timestamp - 0.1) {
      _longTermEntries.add(newEntry)
    }
  }
}