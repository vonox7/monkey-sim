import definitions.WorldState
import definitions.tick
import world.generateSimpleGraz

class Game {
  val world = generateSimpleGraz()
  val worldState = WorldState(time = 6.9, day = 0)

  fun tick() {
    worldState.tick()
    world.actors.forEach { it.tick(world, worldState) }
  }
}