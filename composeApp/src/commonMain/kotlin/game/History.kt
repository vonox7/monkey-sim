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
  val longTermEntries
    get(): List<Entry> {
      return if (_longTermEntries.last().timestamp - _longTermEntries.first().timestamp < 24) {
        _tickEntries // Not enough longterm data, so provide finer resolution
      } else if (_longTermEntries.size < 500) {
        _longTermEntries
      } else {
        // Draw at most 250 entries for performance reasons. Drawing more wouldn't bring more information
        val takeEvery = _longTermEntries.size / 250
        println("Taking every $takeEvery, total ${_longTermEntries.size} entries")
        _longTermEntries.filterIndexed { index, _ -> index % takeEvery == 0 || index == _longTermEntries.size - 1 }
      }
    }

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