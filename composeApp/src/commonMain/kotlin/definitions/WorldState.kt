package definitions

class WorldState(
  var hour: Double, // Time in hours between 0 and 24
  var day: Int, // Time in hours between 0 (monday) and 6 (sunday)
) {
  var timestamp: Double = hour + day * 24
  val isWorkDay: Boolean get() = day < 5
  val isWeekend: Boolean get() = day >= 5
  val isLunchTime: Boolean get() = hour >= 12 && hour < 14
  val isDayTime: Boolean get() = hour >= 7 && hour < 20
  val isSleepTime: Boolean get() = hour >= 22 || hour < 6

  fun tick(elapsedHours: Double) {
    timestamp += elapsedHours
    hour += elapsedHours
    if (hour >= 24) {
      hour = 0.0
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

    val hours = hour.toInt().toString().padStart(2, '0')
    val minutes = ((hour % 1) * 60).toInt().toString().padStart(2, '0')

    return "$day $hours:$minutes"
  }
}