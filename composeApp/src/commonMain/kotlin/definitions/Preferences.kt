package definitions

import kotlin.random.Random

class Preferences(random: Random) {
  companion object {
    fun basicRandom(random: Random) = (random.nextDouble() * random.nextDouble()).coerceIn(0.0, 1.0)
  }

  // All in [0; 1]
  val places = mapOf(
    Gym::class to basicRandom(random),
    Club::class to basicRandom(random),
    Park::class to basicRandom(random),
  )

  val minConnectionCount = random.nextInt(1, 20) * random.nextInt(1, 20).toDouble()
  val minConnectionStrengthSum = random.nextDouble() * 70
}

