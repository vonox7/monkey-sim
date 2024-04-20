package definitions

import kotlin.random.Random

class Actor(
  val name: String,
  val needs: Needs,

  var yearsOfEducation: Double,
  val age: Int,
  val sex: Sex,
  var money: Int,

  var currentPosition: Position,
  var home: Home,
  var work: Work? = null,
  // TODO partner
) {
  val socialConnections: SocialConnections = SocialConnections()

  val perceivedState: State
    get() {
      return if (currentPosition == targetState.targetPlace.position) {
        targetState
      } else {
        // TODO multiple commuting modes (car, bike, walk - depending on money & distance)
        State.Commuting(direction = currentPosition.directionTo(targetState.targetPlace.position, maxTravelSpeed = 1.0))
      }
    }

  var targetState: State.DurationalState = State.DurationalState.Sleeping(0, home)

  sealed class State {
    sealed class DurationalState(var durationLeft: Int, val targetPlace: Place) : State() {
      class Working(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Shopping(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Sleeping(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Eating(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Socializing(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Educating(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
      class Fun(durationLeft: Int, targetPlace: Place) : DurationalState(durationLeft, targetPlace)
    }

    class Commuting(val direction: Direction) : State()
  }

  companion object {
    // TODO name corresponding to sex
    private val firstNames = listOf(
      "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy", "Kevin", "Linda", "Mallory", "Niaj", "Oscar",
      "Peggy", "Quentin", "Rene", "Steve", "Trent", "Ursula", "Victor", "Walter", "Xavier", "Yvonne", "Zelda"
    )

    private val lastNames = listOf(
      "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson",
      "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall",
    )

    fun create(random: Random, home: Home): Actor {
      val age = random.nextInt(0, 80)
      return Actor(
        name = "${firstNames.random(random)} ${lastNames.random(random)}",
        sex = when (random.nextFloat()) {
          in 0.0..0.45 -> Sex.Male
          in 0.4..0.9 -> Sex.Female
          else -> Sex.Other
        },
        needs = Needs.default(),
        yearsOfEducation = if (age <= 6) 0.0 else random.nextDouble(0.0, age - 5.0), // TODO better education system
        age = age,
        currentPosition = home.position,
        home = home,
        money = 1000 * age
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
  var amount: Double,
) {
  class Money(amount: Double) : Need(amount)
  class Food(amount: Double) : Need(amount)
  class Sleep(amount: Double) : Need(amount)
  class Social(amount: Double) : Need(amount)
  class Fun(amount: Double) : Need(amount)

  fun add(amountPerHour: Double, elapsedHours: Double) {
    amount = (amount + amountPerHour * elapsedHours).coerceIn(0.0, 1.0)
  }
}
