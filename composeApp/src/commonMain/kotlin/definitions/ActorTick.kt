package definitions

import definitions.Actor.State.DurationalState.*
import display
import game.Game
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.reflect.KClass

private const val simulationYearInHours = 7 * 24 // 1 year has 7 days to make simulation faster

fun Actor.tick(
  world: World,
  worldState: WorldState,
  elapsedHours: Double,
  actorModifications: Game.ActorModifications,
) {
  decreaseNeeds(elapsedHours)

  handleAge(elapsedHours, actorModifications)

  // Commute
  if (targetState.targetPlace.position != currentPosition) {
    val commuteSpeed = (if (money > 3_000) 600 else 200) * elapsedHours // TODO travel speed more than just by money
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
        money -= if (targetState.targetPlace == home) 10 * elapsedHours else 20 * elapsedHours
      }

      // *4, as 6 hours of education = 1 day of education
      // *7/5, as education doesn't happen on weekends
      is Educating -> yearsOfEducation += elapsedHours / simulationYearInHours * 4 * 7 / 5

      is InThePark -> {
        money -= 3 * elapsedHours
      }

      is AtTheGym -> {
        money -= 5 * elapsedHours
      }

      is AtTheClub -> {
        money -= 30 * elapsedHours
      }

      is Sleeping -> needs.sleep.add(0.125, elapsedHours)
      is Working -> {
        money += 20 * elapsedHours
        needs.workFreeTime.add(0.125, elapsedHours)

        val work = workPlace?.work
        if (elapsedHours > Random.nextDouble(0.0, 1000.0) &&
          work?.let { it.currentWorkingPeople > it.maxPeople / 2 } == true
        ) {
          // You just got fired
          println("$name got fired at $workPlace $work, oh no")
          work.currentWorkingPeople -= 1
          workPlace = null
        }
      }

      is JobHunt -> {
        if (worldState.hour.toInt() in targetState.targetPlace.openHours) {
          val work = targetState.targetPlace.work
          if (workPlace == null && work != null && work.currentWorkingPeople < work.maxPeople && yearsOfEducation >= work.minEducationYears) {
            workPlace = targetState.targetPlace
            work.currentWorkingPeople += 1
            println("$name got hired at ${targetState.targetPlace} $work, yeah. workPlace=$workPlace, work=$work")
          }
        }
      }

      is WatchTv -> {
        // Do nothing

        // Except watching TV at home with partner ... this could lead to babies ;)
        val partner = social.partner
        val place = targetState.targetPlace
        if ((partner?.perceivedState as? WatchTv)?.targetPlace == targetState.targetPlace && place is Home) {
          if (isReproductive && partner.isReproductive && gender != partner.gender) {
            // Decide to reproduce instead of watching TV. But no time for pregnancy, create baby instantly
            if (Random.nextDouble() < elapsedHours * 0.1) {
              val baby = Actor.create(Random, place, ageOverride = 0, lastNameOverride = this.name.split(" ").last())
              println("$name (${age.display()}) and ${partner.name} (${partner.age.display()}) got a baby: $baby")
              actorModifications.babies.add(baby)
            }
          }
        }
      }

      is VisitFriend -> {
        // Do nothing
      }
    }
    return
  }

  // Generate next state
  targetState = generateTargetState(world, worldState, elapsedHours)
}

private fun Actor.decreaseNeeds(elapsedHours: Double) {
  if (perceivedState is Sleeping) {
    needs.food.add(-0.1, elapsedHours)
  } else {
    needs.food.add(-0.10, elapsedHours)
    needs.sleep.add(-0.04, elapsedHours)
  }

  if (perceivedState !is Working) {
    needs.workFreeTime.add(-0.15, elapsedHours)
  }
}

private fun Actor.handleAge(elapsedHours: Double, actorModifications: Game.ActorModifications) {
  age += elapsedHours / simulationYearInHours

  val work = workPlace?.work

  if (age.toInt() !in AgeCategory.ADULT.range && work != null) {
    // Retire
    println("$name is retiring at ${age.display()} from $workPlace $work")
    work.currentWorkingPeople -= 1
    workPlace = null
  }

  if (social.partner == null && age.toInt() in AgeCategory.ADULT.range) {
    // We have no partner ... with increase age we should look more for them
    preferences.minConnectionStrengthSum += elapsedHours * 0.2
  }

  val deathProbabilityPerHour = when (age) {
    in 0.0..60.0 -> 0.000001
    in 60.0..70.0 -> 0.000005
    in 70.0..80.0 -> 0.00001
    in 80.0..85.0 -> 0.00005
    in 85.0..90.0 -> 0.0001
    in 85.0..95.0 -> 0.0005
    in 95.0..Double.MAX_VALUE -> 0.001
    else -> throw Exception("Can't happen")
  }

  // Death
  if (Random.nextDouble() < deathProbabilityPerHour * elapsedHours) {
    println("$name died at age ${age.display()}")

    actorModifications.deaths.add(this)

    // Remove from work
    workPlace?.work?.let { it.currentWorkingPeople -= 1 }
    workPlace = null

    // Remove from partner
    social.partner?.let { partner ->
      partner.social.partner = null
      partner.preferences.minConnectionStrengthSum *= 2 // Other person want's to find new partner
    }

    // Make sure we remove any references so we can GC actors
    social.partner = null
    social.connections.clear()

    // Make sure the actor has no lovePotential any more
    alive = false

    // Don't clean social connections to the actor ... they will fade out over time
  }
}

private fun Actor.generateTargetState(
  world: World,
  worldState: WorldState,
  elapsedHours: Double,
): Actor.State.DurationalState {
  // Satisfy "immediate needs": food, sleep

  if (needs.food.amount < 0.5 && currentPosition == home.position) {
    // Eat at home
    return Eating(1.0, home)
  } else if (needs.food.amount < 0.6 && worldState.isDayTime) {
    // Find some place to eat
    return Eating(0.5, getNearestOpenPlace<FoodShop>(world, worldState) ?: home)
  } else if (needs.food.amount < 0.1) {
    // Go home and eat, I am very hungry
    return Eating(0.5, home)
  }

  if (needs.sleep.amount < 0.3) {
    return Sleeping((8 * (1.0 - needs.sleep.amount)).coerceAtLeast(1.0), home)
  }

  // Do things because it is time to do so: work, education, sleep

  if (worldState.isWorkDay && workPlace?.work?.let { worldState.hour.toInt() in it.workableHours } == true) {
    if (needs.food.amount < 0.7 && worldState.isLunchTime) {
      // Lunch break
      val nearestFoodPlace = getNearestOpenPlace<FoodShop>(world, worldState) // TODO cache, can not be changed
      if (nearestFoodPlace != null && nearestFoodPlace.position.distanceTo(currentPosition) < 100) {
        return Eating(0.5, nearestFoodPlace)
      }
    }
    if (workPlace?.work?.let { worldState.hour.toInt() in it.coreWorkingHours } == true) {
      // Work within core working hours
      return Working(1.0, workPlace!!)
    } else if (needs.workFreeTime.amount < 0.9) {
      // Not enough work for today (outside core working hours)
      return Working(1.0, workPlace!!)
    }
  }

  if (worldState.isWorkDay && workPlace == null && age.toInt() in 6..25) {
    val nearestSchool = getNearestOpenPlace<University>(world, worldState)
    if (nearestSchool != null) {
      return Educating(6.0, nearestSchool)
    }
  }

  if (worldState.isSleepTime) {
    val sleepHours = when (age) {
      in 0.0..8.0 -> 10.0
      in 9.0..14.0 -> 9.0
      in 15.0..30.0 -> 8.0
      in 30.0..60.0 -> 7.5
      in 30.0..60.0 -> 7.0
      else -> 6.5
    }
    return Sleeping(sleepHours, home)
  }

  // Go out and eat if we are a little bit hungry
  if (needs.food.amount < 0.5) {
    val nearestFoodPlace = getNearestOpenPlace<FoodShop>(world, worldState)
    if (nearestFoodPlace != null) {
      return Eating(1.0, nearestFoodPlace)
    }
  }

  // Find a job if you don't have one (and have less than twice the initial money)
  if (age.toInt() in AgeCategory.ADULT.range && workPlace == null && money < age * 100 * 2 && worldState.isWorkDay) {
    val potentialWorkPlace = (
        // Near places people know
        listOf(
          getNearestPlace<Industry>(world),
          getNearestOpenPlace<Industry>(world, worldState),
          getNearestPlace<FoodShop>(world),
          getNearestOpenPlace<FoodShop>(world, worldState),
        ) +
            // Places where friends work
            social.connections.keys.mapNotNull { it.workPlace } +
            // Random places they find in the world
            world.places.values.map { it.random() }
        )
      .filterNotNull()

    return JobHunt(1.0, potentialWorkPlace.random())
  }

  // Socialize
  if (social.connections.entries.sumOf { it.value } < preferences.minConnectionStrengthSum) {
    // Either visit a friend
    val friendToVisit = social.connections.entries.filter { (friend, strength) ->
      strength > 1 && // Visit people we already met more than once in public
          friend.targetState.targetPlace == friend.home && // Visit friend if he is currently at home or traveling home
          friend.targetState !is Sleeping && // Visit friends who are not sleeping
          (age - friend.age).absoluteValue < 10 // Visit only friends who have roughly the same age
    }.randomOrNull()?.key
    if (friendToVisit != null) {
      return VisitFriend(2.0, friendToVisit.home)
    }

    // Or go to a public place
    val place = (listOf(Club::class, Gym::class, Park::class) +
        if (age.toInt() in 6..50 && workPlace == null && worldState.isWorkDay) listOf(University::class) else emptyList()
        )
      .flatMap { clazz ->
        val places = getNearestOpenPlaces(clazz, world, worldState)
        val preference = preferences.places[clazz]!!
        places.map { place -> place to preference }
      }
      .maxByOrNull { (place, preference) -> preference * place.position.distanceTo(currentPosition) }
      ?.first


    return when (place) {
      is Club -> AtTheClub(2.5, place)
      is Gym -> AtTheGym(2.0, place)
      is Park -> InThePark(1.5, place)
      is University -> Educating(6.0, place)
      else -> throw Exception("Can't happen")
    }
  }

  return WatchTv(1.0, home)
}

inline fun <reified T : Place> Actor.getNearestPlace(world: World): T? {
  // TODO optimize so we don't need to filter ever time and iterate over all places every time
  return world.places[T::class]!!.minByOrNull { it.position.distanceTo(currentPosition) } as T?
}

fun <T : Place> Actor.getNearestOpenPlaces(
  clazz: KClass<out T>,
  world: World,
  worldState: WorldState,
  limit: Int = 5,
): List<T> {
  // TODO optimize so we don't need to filter ever time and iterate over all places every time
  @Suppress("UNCHECKED_CAST") val places = world.places[clazz]!! as List<T>
  return places.filter { worldState.hour.toInt() in it.openHours }
      .sortedBy { it.position.distanceTo(currentPosition) }
    .take(limit)
}

inline fun <reified T : Place> Actor.getNearestOpenPlaces(
  world: World,
  worldState: WorldState,
  limit: Int = 5,
): List<T> {
  return getNearestOpenPlaces(T::class, world, worldState, limit)
}

inline fun <reified T : Place> Actor.getNearestOpenPlace(world: World, worldState: WorldState): T? {
  // TODO optimize so we don't need to filter ever time and iterate over all places every time
  @Suppress("UNCHECKED_CAST") val places = world.places[T::class]!! as List<T>
  return places.filter { worldState.hour.toInt() in it.openHours }
    .minByOrNull { it.position.distanceTo(currentPosition) }
}
