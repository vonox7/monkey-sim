package definitions

import definitions.Actor.State.DurationalState.*

fun Actor.tick(world: World, worldState: WorldState, elapsedHours: Double) {
  decreaseNeeds(elapsedHours)

  // Commute
  if (targetState.targetPlace.position != currentPosition) {
    val commuteSpeed = (if (money > 1000) 600 else 200) * elapsedHours // TODO travel speed more than just by money
    val direction = currentPosition.directionTo(targetState.targetPlace.position, maxTravelSpeed = commuteSpeed)
    currentPosition += Position(direction.dx, direction.dy)
    return
  }

  // Consume current state
  if (targetState.hoursLeft > 0) {
    targetState.hoursLeft = (targetState.hoursLeft - elapsedHours).coerceAtLeast(0.0)
    when (targetState) {
      is Eating -> {
        needs.food.add(2.0, elapsedHours)
        money -= 20 * elapsedHours
      }

      is Educating -> yearsOfEducation += 0.001
      is Fun -> {
        money -= 20 * elapsedHours
      }

      is Shopping -> {
        money -= 200 * elapsedHours
      }

      is Sleeping -> needs.sleep.add(0.125, elapsedHours)
      is Socializing -> TODO()
      is Working -> money += 20 * elapsedHours
    }
    return
  }

  // Generate next state
  targetState = generateTargetState(world, worldState)
}

private fun Actor.decreaseNeeds(elapsedHours: Double) {
  if (perceivedState is Sleeping) {
    needs.food.add(-0.1, elapsedHours)
  } else {
    needs.food.add(-0.10, elapsedHours)
    needs.sleep.add(-0.04, elapsedHours)
  }
}

private fun Actor.generateTargetState(world: World, worldState: WorldState): Actor.State.DurationalState {
  // Satisfy "immediate needs": food, sleep

  if (needs.food.amount < 0.3 || (needs.food.amount < 0.6 && !worldState.isCoreWorkingHours)) {
    if (currentPosition == home.position) {
      // Eat at home
      return Eating(1.0, home)
    } else if (worldState.isShopOpenHours) {
      // Find some place to eat
      return Eating(0.5, getNearestPlace<FoodShop>(world))
    } else if (needs.food.amount < 0.1) {
      // Go home and eat, I am very hungry
      return Eating(0.5, home)
    }
  }

  if (needs.sleep.amount < 0.3) {
    return Sleeping((8 * (1.0 - needs.sleep.amount)).coerceAtLeast(1.0), home)
  }

  // Do things because it is time to do so: work, sleep

  if (worldState.isWorkingHours && worldState.isWorkDay && work != null) {
    if (needs.food.amount < 0.7 && worldState.isLunchTime) {
      // Lunch break
      val nearestFoodPlace = getNearestPlace<FoodShop>(world) // TODO cache, can not be changed
      if (nearestFoodPlace.position.distanceTo(currentPosition) < 100) {
        return Eating(0.5, nearestFoodPlace)
      }
    }
    return Working(1.0, work!!)
  }

  if (worldState.isSleepTime) {
    return Sleeping(7.0, home)
  }

  // Go out and eat if we are a little bit hungry
  if (needs.food.amount < 0.5) {
    return Eating(1.0, getNearestPlace<FoodShop>(world))
  }

  // Do something fun
  return Fun(1.0, getNearestPlace<Place>(world)) // TODO where to do fun?!? :D
}

inline fun <reified T : Place> Actor.getNearestPlace(world: World): T {
  // TODO optimize so we don't need to filter ever time and iterate over all places every time
  return world.places.filterIsInstance<T>().minBy { it.position.distanceTo(currentPosition) }
}
