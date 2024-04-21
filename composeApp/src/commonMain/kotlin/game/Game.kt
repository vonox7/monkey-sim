package game

import definitions.Actor
import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(hour = 6.9, day = 0)
  val history = History().apply { add(world, worldState) }

  class ActorModifications(val babies: MutableList<Actor>, val deaths: MutableList<Actor>)

  fun tick(elapsedHours: Double) {
    worldState.tick(elapsedHours)

    val actorModifications = ActorModifications(mutableListOf(), mutableListOf())
    world.actors.forEach { it.tick(world, worldState, elapsedHours, actorModifications) }
    // Add and remove actors after all actors have ticked so we don't modify the list while iterating
    actorModifications.babies.forEach { world.actors.add(it) }
    actorModifications.deaths.forEach { world.actors.remove(it) }

    buildSocialConnections(elapsedHours)
    decaySocialConnections(elapsedHours)
    history.add(world, worldState)
  }
}