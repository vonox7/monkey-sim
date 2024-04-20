package definitions

import display
import kotlin.random.Random

class Actor(
  random: Random,
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

  val preferences = Preferences(random)

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

  sealed class State(val socializeFactor: Double = 0.0) {
    abstract override fun toString(): String;

    sealed class DurationalState(var hoursLeft: Double, val targetPlace: Place, formSocialConnectionsPerHour: Double = 0.0) :
      State(formSocialConnectionsPerHour) {
      override fun toString(): String = "${this::class.simpleName!!} at ${targetPlace}, hours left: ${hoursLeft.display()}"

      class Working(hoursLeft: Double, targetPlace: Place) :
        DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.001)

      class Shopping(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace)

      class Sleeping(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace)

      class Eating(hoursLeft: Double, targetPlace: Place) :
        DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.001)

      class Educating(hoursLeft: Double, targetPlace: Place) : DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.004)

      class InThePark(hoursLeft: Double, targetPlace: Park) : DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.006)

      class AtTheClub(hoursLeft: Double, targetPlace: Club) : DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.010)

      class AtTheGym(hoursLeft: Double, targetPlace: Gym) : DurationalState(hoursLeft, targetPlace, formSocialConnectionsPerHour = 0.002)

      class WatchTv(hoursLeft: Double, targetPlace: Home) : DurationalState(hoursLeft, targetPlace)
    }

    class Commuting(val direction: Direction) : State() {
      override fun toString(): String = "Commuting to $direction"
    }

    companion object {
      val allStates = listOf(
        DurationalState.Working::class,
        DurationalState.Shopping::class,
        DurationalState.Sleeping::class,
        DurationalState.Eating::class,
        DurationalState.Educating::class,
        Commuting::class,
        DurationalState.InThePark::class,
        DurationalState.AtTheClub::class,
        DurationalState.AtTheGym::class,
        DurationalState.WatchTv::class
      )
    }
  }

  companion object {
    // TODO name corresponding to sex
    private val firstNames = mapOf(
      Sex.Male to listOf(
        "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles", "Daniel", "Matthew", "Anthony",
        "Donald", "Mark", "Paul", "Steven", "Andrew", "Kenneth", "Joshua", "George", "Kevin", "Brian", "Edward", "Ronald", "Timothy"
      ),
      Sex.Female to listOf(
        "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen", "Nancy", "Lisa", "Betty",
        "Dorothy", "Sandra", "Ashley", "Kimberly", "Donna", "Emily", "Michelle", "Carol", "Amanda", "Melissa", "Deborah", "Stephanie",
      ),
      Sex.Other to listOf(
        "Alex", "Ali",
      )
    )

    private val lastNames = listOf(
      "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson",
      "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall",
    )

    fun create(random: Random, home: Home): Actor {
      val age = when (random.nextInt(0, 5)) {
        0 -> random.nextInt(0, 35)
        1 -> random.nextInt(35, 50)
        2 -> random.nextInt(50, 62)
        3 -> random.nextInt(62, 80)
        4 -> random.nextInt(80, 100)
        else -> throw IllegalArgumentException("Can't happen")
      }
      val sex = when (random.nextFloat()) {
        in 0.0..0.48 -> Sex.Male
        in 0.48..0.95 -> Sex.Female
        else -> Sex.Other
      }
      return Actor(
        random,
        name = "${firstNames[sex]!!.random(random)} ${lastNames.random(random)}",
        sex = sex,
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
  val workFreeTime: Need.WorkFreeTime,
) {
  companion object {
    fun default() = Needs(
      food = Need.Food(0.5),
      sleep = Need.Sleep(1.0),
      workFreeTime = Need.WorkFreeTime(0.0),
    )
  }
}
// isn't money a need? and should we remove fun and use social instead?

sealed class Need(
  /** Between [0;1] */
  var amount: Double, // Do not modify directly, but use add() function
) {
  class Food(amount: Double) : Need(amount)
  class Sleep(amount: Double) : Need(amount)
  class WorkFreeTime(amount: Double) : Need(amount)

  fun add(amountPerHour: Double, elapsedHours: Double) {
    amount = (amount + amountPerHour * elapsedHours).coerceIn(0.0, 1.0)
  }
}
