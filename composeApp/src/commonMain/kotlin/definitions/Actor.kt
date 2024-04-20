package definitions

import kotlin.random.Random

class Actor(
  val name: String,
  val needs: Needs,

  val yearsOfEducation: Int,
  val age: Int,
  val sex: Sex,
  var currentPosition: Position,
  var home: Home,
  var work: Work? = null,
  // TODO partner
) {
  val socialConnections: SocialConnections = SocialConnections()

  companion object {
    private val firstNames = listOf(
      "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy", "Kevin", "Linda", "Mallory", "Niaj", "Oscar",
      "Peggy", "Quentin", "Rene", "Steve", "Trent", "Ursula", "Victor", "Walter", "Xavier", "Yvonne", "Zelda"
    )

    private val lastNames = listOf(
      "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson",
      "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall",
    )

    fun randomInWorld(worldWidth: Int, worldHeight: Int, home: Home, work: Work?): Actor {
      return Actor(
        name = "${firstNames.random()} ${lastNames.random()}",
        sex = when (Random.nextFloat()) {
          in 0.0..0.45 -> Sex.Male
          in 0.4..0.9 -> Sex.Female
          else -> Sex.Other
        },
        needs = Needs.default(),
        yearsOfEducation = Random.nextInt(0, 20),
        age = Random.nextInt(0, 80),
        currentPosition = Position(Random.nextInt(0, worldWidth), Random.nextInt(0, worldHeight)),
        home = home,
        work = work,
      )
    }
  }
}

enum class Sex {
  Male, Female, Other;
}

class Needs(
  val money: Need.Money,
  val food: Need.Food,
  val sleep: Need.Sleep,
  val social: Need.Social,
) {
  companion object {
    fun default() = Needs(
      money = Need.Money(0.5),
      food = Need.Food(0.5),
      sleep = Need.Sleep(0.5),
      social = Need.Social(0.5),
    )
  }
}

sealed class Need(
  /** Between [0;1] */
  val amount: Double,
) {
  class Money(amount: Double) : Need(amount)
  class Food(amount: Double) : Need(amount)
  class Sleep(amount: Double) : Need(amount)
  class Social(amount: Double) : Need(amount)
  class Fun(amount: Double) : Need(amount)
}
