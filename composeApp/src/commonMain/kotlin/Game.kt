import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(time = 16.9, day = 0)

  fun tick(elapsedHours: Double) {
    worldState.tick(elapsedHours)
    world.actors.forEach { it.tick(world, worldState, elapsedHours) }
  }
}