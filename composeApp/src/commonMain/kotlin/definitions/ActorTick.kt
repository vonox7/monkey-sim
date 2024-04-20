package definitions

import definitions.Actor.State.DurationalState.*

fun Actor.tick(world: World, worldState: WorldState) {
  // Commute
  if (this.targetState.targetPlace.position != this.currentPosition) {
    val direction =
      this.currentPosition.directionTo(this.targetState.targetPlace.position, maxTravelSpeed = 0.1) // TODO travel speed
    this.currentPosition += Position(direction.dx, direction.dy)
    return
  }

  // Consume current state
  if (this.targetState.durationLeft > 0) {
    this.targetState.durationLeft -= 1
    return
  }

  // Generate next state
  this.targetState = generateTargetState(world, worldState)

}

private fun Actor.generateTargetState(world: World, worldState: WorldState): Actor.State.DurationalState {
  // Satisfy "immediate needs": food, sleep

  if (this.needs.food.amount < 0.3) {
    if (this.currentPosition == this.home.position) {
      // Eat at home
      return Eating(1, this.home)
    } else if (worldState.isShopOpenHours) {
      // Find some place to eat
      return Eating(1, getNearestPlace<FoodShop>(world))
    } else if (this.needs.food.amount < 0.1) {
      // Go home and eat, I am very hungry
      return Eating(1, this.home)
    }
  }

  if (this.needs.sleep.amount < 0.3) {
    return Sleeping((8 * (1.0 - this.needs.sleep.amount)).toInt().coerceAtLeast(1), this.home)
  }

  // Do things because it is time to do so: work, sleep

  if (worldState.isWorkingHours && worldState.isWorkDay && this.work != null) {
    return Working(8, this.work!!)
  }

  if (worldState.isSleepTime) {
    return Sleeping(8, this.home)
  }

  // Go out and eat if we are a little bit hungry
  if (this.needs.food.amount < 0.5) {
    return Eating(1, getNearestPlace<FoodShop>(world))
  }

  // Do something fun
  return Fun(8, getNearestPlace<Place>(world)) // TODO where to do fun?!? :D
}

inline fun <reified T : Place> Actor.getNearestPlace(world: World): T {
  // TODO optimize so we don't need to filter ever time and iterate over all places every time
  return world.places.filterIsInstance<T>().minBy { it.position.distanceTo(this.currentPosition) }
}
