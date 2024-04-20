package definitions

import display
import kotlin.random.Random

class Actor(
  val name: String,
  val needs: Needs,

  var yearsOfEducation: Double,
  val age: Int,
  val sex: Sex,
  var money: Double,

  var currentPosition: Position,
  var home: Home,
  var workPlace: Place? = null, // Needs to be manually synchronized with Place.Work.currentWorkingPeople
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

  var targetState: State.DurationalState = State.DurationalState.Sleeping(0.0, home)

  sealed class State {
    abstract override fun toString(): String;

    sealed class DurationalState(var hoursLeft: Double, val targetPlace: Place) : State() {
      override fun toString(): String = "${mainToString()}, hours left: ${hoursLeft.display()}"
      abstract fun mainToString(): String

      class Working(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Working at $targetPlace"
      }

      class Shopping(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Shopping at $targetPlace"
      }

      class Sleeping(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Sleeping at $targetPlace"
      }

      class Eating(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Eating at $targetPlace"
      }

      class Socializing(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Socializing at $targetPlace"
      }

      class Educating(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Educating at $targetPlace"
      }

      class Fun(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace) {
        override fun mainToString(): String = "Having fun at $targetPlace"
      }
    }

    class Commuting(val direction: Direction) : State() {
      override fun toString(): String = "Commuting to $direction"
    }
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
        money = 1000.0 * age
      )
    }
  }
}

enum class Sex {
  Male, Female, Other;
}

class Needs(
  val food: Need.Food,
  val sleep: Need.Sleep,
  val social: Need.Social,
  val workFreeTime: Need.WorkFreeTime,
) {
  companion object {
    fun default() = Needs(
      food = Need.Food(0.5),
      sleep = Need.Sleep(1.0),
      social = Need.Social(0.5),
      workFreeTime = Need.WorkFreeTime(0.0),
    )
  }
}

sealed class Need(
  /** Between [0;1] */
  var amount: Double, // Do not modify directly, but use add() function
) {
  class Money(amount: Double) : Need(amount)
  class Food(amount: Double) : Need(amount)
  class Sleep(amount: Double) : Need(amount)
  class Social(amount: Double) : Need(amount)
  class WorkFreeTime(amount: Double) : Need(amount)
  class Fun(amount: Double) : Need(amount)

  fun add(amountPerHour: Double, elapsedHours: Double) {
    amount = (amount + amountPerHour * elapsedHours).coerceIn(0.0, 1.0)
  }
}
