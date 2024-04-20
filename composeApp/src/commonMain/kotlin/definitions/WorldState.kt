package definitions

class WorldState(
  var time: Double, // Time in hours between 0 and 24
  var day: Int, // Time in hours between 0 (monday) and 6 (sunday)
) {
  val isWorkDay: Boolean get() = day < 5
  val isWeekend: Boolean get() = day >= 5
  val isLunchTime: Boolean get() = time >= 12 && time < 14
  val isDayTime: Boolean get() = time >= 7 && time < 20
  val isSleepTime: Boolean get() = time >= 22 || time < 6

  fun tick(elapsedHours: Double) {
    time += elapsedHours
    if (time >= 24) {
      time = 0.0
      day = (day + 1) % 7
    }
  }

  override fun toString(): String {
    val day = when (day) {
      0 -> "Monday"
      1 -> "Tuesday"
      2 -> "Wednesday"
      3 -> "Thursday"
      4 -> "Friday"
      5 -> "Saturday"
      6 -> "Sunday"
      else -> throw Exception()
    }

    val hours = time.toInt().toString().padStart(2, '0')
    val minutes = ((time % 1) * 60).toInt().toString().padStart(2, '0')

    return "$day $hours:$minutes"
  }
}