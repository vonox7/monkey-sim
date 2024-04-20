package world

import definitions.*
import kotlin.random.Random

fun generateSimpleGraz(): World {
  // Area codes from 0 to 9
  class Area(val x: Int, val y: Int, val density: Int) {
    fun toPosition(random: Random): Position {
      val shift = when (random.nextInt(0, 4)) {
        0 -> if (x % 2 == 0) 20 else 60
        1 -> if (y % 2 == 0) 40 else 80
        2 -> 90
        3 -> if (x == 0 || y == 0 || x == 9 || y == 9) 30 else 120
        else -> throw Exception()
      }
      return Position(
        (50.0 + 90 * x + random.nextInt(-shift, shift)).coerceIn(10.0, 990.0),
        (50.0 + 90 * y + random.nextInt(-shift, shift)).coerceIn(10.0, 990.0),
      )
    }
  }

  val homeAreas = listOf(
    Area(0, 5, 10),
    Area(0, 6, 10),
    Area(0, 7, 10),
    Area(1, 0, 10),
    Area(1, 1, 10),
    Area(2, 3, 15),
    Area(2, 4, 20),
    Area(2, 5, 20),
    Area(2, 6, 20),
    Area(2, 7, 15),
    Area(2, 8, 10),
    Area(3, 3, 20),
    Area(3, 5, 30),
    Area(3, 6, 20),
    Area(3, 7, 10),
    Area(4, 4, 10),
    Area(4, 5, 10),
    Area(4, 6, 10),
    Area(4, 7, 10),
    Area(5, 5, 10),
    Area(5, 6, 10),
    Area(5, 7, 10),
    Area(6, 5, 5),
    Area(6, 8, 5),
    Area(7, 5, 5),
    Area(7, 8, 5),
    Area(8, 5, 4),
    Area(8, 9, 3),
    Area(9, 5, 3),
    Area(9, 9, 2),
  )

  val industryAreas = listOf(
    Area(1, 9, 10),
    Area(2, 8, 10),
    Area(2, 9, 10),
    Area(3, 4, 10),
    Area(3, 5, 10),
    Area(3, 6, 10),
    Area(3, 8, 10),
    Area(3, 9, 10),
    Area(4, 4, 10),
    Area(4, 5, 10),
    Area(4, 6, 10),
    Area(4, 7, 10),
    Area(4, 8, 20),
    Area(4, 9, 20),
    Area(5, 7, 30),
    Area(5, 8, 30),
    Area(6, 4, 10),
    Area(6, 8, 20),
    Area(6, 9, 20),
    Area(7, 7, 10),
    Area(7, 8, 10),
    Area(7, 9, 10),
    Area(8, 7, 10),
  )

  val foodShopAreas = listOf(
    Area(1, 2, 10),
    Area(1, 8, 10),
    Area(2, 2, 10),
    Area(2, 3, 20),
    Area(2, 4, 20),
    Area(2, 5, 20),
    Area(3, 4, 40),
    Area(3, 5, 40),
    Area(3, 6, 20),
    Area(4, 3, 10),
    Area(5, 7, 10),
  )

  val educationAreas = listOf(
    Area(1, 5, 5),
    Area(3, 4, 10),
    Area(3, 5, 10),
    Area(3, 6, 10),
    Area(4, 3, 30),
    Area(5, 3, 10),
  )

  val clubAreas = listOf(
    Area(2, 2, 3),
    Area(2, 4, 5),
    Area(3, 3, 10),
    Area(3, 4, 10),
    Area(4, 3, 30),
  )

  val sportAreas = listOf(
    Area(1, 1, 2),
    Area(3, 7, 2),
    Area(4, 6, 4),
    Area(5, 3, 2),
    Area(8, 5, 2),
    Area(8, 8, 2),
  )

  val parkAreas = listOf(
    Area(2, 3, 1),
    Area(2, 7, 2),
    Area(3, 2, 2),
    Area(3, 5, 2),
    Area(4, 3, 3),
  )

  val random = Random(123456)

  val places = mutableListOf<Place>()
  val actors = mutableListOf<Actor>()

  homeAreas.forEach { homeArea ->
    repeat(homeArea.density * 10) {
      val homePlace = Home(homeArea.toPosition(random))
      places += homePlace
      repeat(random.nextInt(1, 3)) {
        actors += Actor.create(random, homePlace)
      }
    }
  }

  industryAreas.forEach { workArea ->
    repeat(workArea.density) {
      val coreWorkingHours = random.nextInt(7, 10)..random.nextInt(14, 16)
      val minEducationYears = random.nextInt(0, 15)
      places += Industry(
        workArea.toPosition(random), openHours = 0..0, work = Place.Work(
          maxPeople = random.nextInt(1, 10) * random.nextInt(1, 10),
          minEducationYears = minEducationYears,
          salaryPerHour = random.nextDouble(10.0, 50.0) + minEducationYears * 3,
          coreWorkingHours = coreWorkingHours,
          workableHours = (coreWorkingHours.first - random.nextInt(0, 5)).coerceAtLeast(6)..
              (coreWorkingHours.last + random.nextInt(0, 5)).coerceAtMost(20),
        )
      )
    }
  }

  foodShopAreas.forEach { shopArea ->
    repeat(shopArea.density) {
      val position = shopArea.toPosition(random)
      val openHours = random.nextInt(6, 8)..random.nextInt(18, 20)
      val work = Place.Work(
        maxPeople = random.nextInt(1, 3),
        minEducationYears = 0,
        salaryPerHour = random.nextDouble(10.0, 50.0),
        coreWorkingHours = openHours,
        workableHours = openHours,
      )
      places += FoodShop(position, openHours, work)
    }
  }

  educationAreas.forEach { educationArea ->
    repeat(educationArea.density) {
      places += University(
        educationArea.toPosition(random), openHours = 8..16, work = Place.Work(
          maxPeople = random.nextInt(3, 30),
          minEducationYears = 12,
          salaryPerHour = random.nextDouble(30.0, 60.0),
          coreWorkingHours = 9..14,
          workableHours = 8..16,
        )
      )
    }
  }

  clubAreas.forEach { clubArea ->
    repeat(clubArea.density) {
      places += Club(
        // TODO openHours & workingHours should be a list of ranges, so we can also work before and after midnight
        clubArea.toPosition(random), openHours = 20..24, work = Place.Work(
          maxPeople = random.nextInt(2, 6),
          minEducationYears = 0,
          salaryPerHour = random.nextDouble(10.0, 40.0),
          coreWorkingHours = 20..24,
          workableHours = 20..24,
        )
      )
    }
  }

  sportAreas.forEach { sportArea ->
    repeat(sportArea.density) {
      places += Gym(
        sportArea.toPosition(random), openHours = 6..22, work = Place.Work(
          maxPeople = random.nextInt(1, 5),
          minEducationYears = 0,
          salaryPerHour = random.nextDouble(10.0, 30.0),
          coreWorkingHours = 8..20,
          workableHours = 6..22,
        )
      )
    }
  }

  parkAreas.forEach { parkArea ->
    repeat(parkArea.density) {
      places += Park(parkArea.toPosition(random))
    }
  }

  // Randomly assign people work
  val workPlaces = places.filter { it.work != null }
  actors.forEach { actor ->
    if (actor.age > 15) {
      val workPlace = workPlaces.shuffled(random)
        .firstOrNull { it.work!!.currentWorkingPeople < it.work.maxPeople && it.work.minEducationYears <= actor.yearsOfEducation.toInt() }
      if (workPlace != null) {
        actor.workPlace = workPlace
        actor.workPlace!!.work!!.currentWorkingPeople++
      }
    }
  }

  println("All people: ${actors.count()}, working people: ${workPlaces.sumOf { it.work!!.currentWorkingPeople }}, available work places: ${workPlaces.sumOf { it.work!!.maxPeople }}")

  return World(
    width = 1000,
    height = 1000,
    places = places.groupBy { it::class },
    actors = actors,
  )
}