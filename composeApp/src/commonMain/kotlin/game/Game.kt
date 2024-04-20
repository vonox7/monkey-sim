package game

import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(timestamp = 0.0, hour = 6.9, day = 0)
  val history = History().apply { add(world, worldState) }

  fun tick(elapsedHours: Double) {
    worldState.tick(elapsedHours)
    world.actors.forEach { it.tick(world, worldState, elapsedHours) }
    buildSocialConnections(elapsedHours)
    decaySocialConnections(elapsedHours)
    history.add(world, worldState)
  }
}