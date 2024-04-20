package definitions

import androidx.compose.ui.graphics.Color

sealed class Place(
  val position: Position,
  val openHours: IntRange,
  val work: Work?,
  val zIndex: Int,
  val color: Color,
) {
  override fun toString(): String {
    return "${this::class.simpleName} at $position"
  }

  init {
    require(openHours.first >= 0 && openHours.last <= 24)
    require(openHours.first <= openHours.last)
  }

  class Work(
    val maxPeople: Int,
    val minEducationYears: Int,
    val salaryPerHour: Double,
    val coreWorkingHours: IntRange,
    val workableHours: IntRange,
  ) {
    init {
      require(minEducationYears >= 0)
      require(salaryPerHour >= 0)
      require(coreWorkingHours.first >= 0 && coreWorkingHours.last <= 24)
      require(coreWorkingHours.first <= coreWorkingHours.last)
      require(workableHours.first >= 0 && workableHours.last <= 24)
      require(workableHours.first <= workableHours.last)
      require(coreWorkingHours.first in workableHours)
      require(coreWorkingHours.last in workableHours)
    }
  }
}

class Industry(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 1, Color(0xFF4e4553))

class FoodShop(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 2, Color(0xFF359750))

class Club(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 3, Color(0xFFf8b594))

class Gym(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 4, Color(0xFF94f8e3))

class University(position: Position, openHours: IntRange, work: Work) :
  Place(position, openHours, work, zIndex = 5, Color(0xFFf8e394))

class Park(position: Position) :
  Place(position, openHours = 0..23, work = null, zIndex = 6, color = Color(0xFF94f8b5))

class Home(position: Position) :
  Place(position, openHours = 0..23, work = null, zIndex = 7, color = Color(0xFF94bdf8))