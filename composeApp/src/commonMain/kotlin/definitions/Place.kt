package definitions

import androidx.compose.ui.graphics.Color

sealed class Place(
  val position: Position,
  val openHours: IntRange, // TODO respect that when people go somewhere and leave if closes. Also do weekends?
  val work: Work?,
  val zIndex: Int,
  val color: Color,
) {
  override fun toString(): String {
    return "${this::class.simpleName}"
  }

  init {
    require(openHours.first >= 0 && openHours.last <= 24)
    require(openHours.first <= openHours.last)
  }

  class Work(
    val maxPeople: Int,
    val minEducationYears: Int,
    val salaryPerHour: Int,
    val coreWorkingHours: IntRange,
    val workableHours: IntRange,
  ) {
    var currentWorkingPeople = 0 // Needs to be manually synchronized with Actor.work

    init {
      require(maxPeople > 0) // If 0 set work to null
      require(minEducationYears >= 0)
      require(salaryPerHour >= 0)
      require(coreWorkingHours.first >= 0 && coreWorkingHours.last <= 24)
      require(coreWorkingHours.first <= coreWorkingHours.last)
      require(workableHours.first >= 0 && workableHours.last <= 24)
      require(workableHours.first <= workableHours.last)
      require(coreWorkingHours.first in workableHours)
      require(coreWorkingHours.last in workableHours)
    }

    override fun toString(): String {
      return "$salaryPerHour€/h ($currentWorkingPeople/$maxPeople people, min $minEducationYears years education)"
    }
  }
}

class Industry(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 1, Color(0xFF4e4553)) // grey

class FoodShop(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 2, Color(0xFF359750)) // green

class Club(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 3, Color(0xFFf8b594)) // light orange

class Gym(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 4, Color(0xFF94f8e3)) // light blue

class School(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 5, Color(0xFFf8e394)) // yellow

class Park(position: Position, openHours: IntRange) :
  Place(position, openHours, work = null, zIndex = 6, color = Color(0xAB68b35b)) // saturated light green

class Home(position: Position) :
  Place(position, openHours = 0..23, work = null, zIndex = 7, color = Color(0xFF94bdf8)) {
  val residents: MutableList<Actor> = mutableListOf() // Needs to be manually synchronized with Actor.home
}