package game

import definitions.Actor
import definitions.WorkingCategory
import definitions.World
import definitions.WorldState
import kotlin.reflect.KClass

class History {
  open class TickEntry(
    val stateToPeopleCount: Map<KClass<out Actor.State>, Int>,
    val worldPopulation: Int,
    val timestamp: Double,
  )

  class LongTermEntry(
    stateToPeopleCount: Map<KClass<out Actor.State>, Int>,
    worldPopulation: Int,
    timestamp: Double,
    val peopleWithPartner: Int,
    val workingInfo: Map<WorkingCategory, Int>,
  ) : TickEntry(stateToPeopleCount, worldPopulation, timestamp)

  // TickEntries are stored for the last 24 hours, but one per simulation tick
  private val _tickEntries: MutableList<TickEntry> = mutableListOf()
  val tickEntries get(): List<TickEntry> = _tickEntries

  // Long term entries are stored every few minutes, but infinite (so memory grows here linearly)
  private val _longTermEntries: MutableList<LongTermEntry> = mutableListOf()
  val longTermEntries
    get(): List<LongTermEntry> {
      return if (_longTermEntries.size < 500) {
        _longTermEntries
      } else {
        // Draw at most 250 entries for performance reasons. Drawing more wouldn't bring more information
        val takeEvery = _longTermEntries.size / 250
        _longTermEntries.filterIndexed { index, _ -> index % takeEvery == 0 || index == _longTermEntries.size - 1 }
      }
    }

  fun add(world: World, worldState: WorldState) {

    val currentStates: Map<KClass<out Actor.State>, Int> =
      world.actors.groupingBy { actor -> actor.perceivedState::class }.eachCount()

    val newEntry = TickEntry(
      Actor.State.allStates.associateWith { currentStates[it] ?: 0 },
      world.actors.size,
      worldState.timestamp,
    )
    _tickEntries.add(newEntry)

    // We only keep the last 24 hours for the tick entries
    _tickEntries.removeAll { it.timestamp < worldState.timestamp - 24 }

    // Add an entry every few minutes to the long term entries
    // Here we can add more information which might have a performance impact
    if (_longTermEntries.isEmpty() || _longTermEntries.last().timestamp < worldState.timestamp - 0.1) {
      val currentWorkingCategory = world.actors.groupingBy { it.workingCategory }.eachCount()

      val newLongTermEntry = LongTermEntry(
        newEntry.stateToPeopleCount,
        newEntry.worldPopulation,
        newEntry.timestamp,
        peopleWithPartner = world.actors.count { it.social.partner != null },
        workingInfo = WorkingCategory.entries.associateWith { currentWorkingCategory[it] ?: 0 }
      )

      _longTermEntries.add(newLongTermEntry)
    }
  }
}