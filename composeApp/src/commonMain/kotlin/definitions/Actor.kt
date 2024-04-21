package definitions

import androidx.compose.ui.graphics.Color
import definitions.WorkingCategory.*
import kotlin.random.Random

class Actor(
  random: Random,
  val name: String,
  val needs: Needs,

  var yearsOfEducation: Double,
  var age: Double, // In years
  val gender: Gender,
  var money: Double,

  var currentPosition: Position,
  var home: Home,
  var workPlace: Place? = null, // Needs to be manually synchronized with Place.Work.currentWorkingPeople
) {
  val social: SocialConnections = SocialConnections()

  val preferences = Preferences(gender, random)

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

  val workingCategory
    get(): WorkingCategory = when (age) {
      in AgeCategory.CHILD.startAge..AgeCategory.CHILD.endAge -> CHILD
      in AgeCategory.ADULT.startAge..AgeCategory.ADULT.endAge -> if (workPlace != null) EMPLOYED else UNEMPLOYED
      in AgeCategory.SENIOR.startAge..AgeCategory.SENIOR.endAge -> RETIRED
      else -> UNEMPLOYED
    }

  override fun toString(): String {
    return name
  }

  sealed class State(
    val formSocialConnectionsPerHour: Double = 0.0,
  ) {

    abstract override fun toString(): String

    sealed class DurationalState(
      var hoursLeft: Double,
      val targetPlace: Place,
      formSocialConnectionsPerHour: Double = 0.0,
    ) : State(formSocialConnectionsPerHour) {

      override fun toString(): String = "${this::class.simpleName!!} at $targetPlace"

      class Working(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.5,
      )

      class JobHunt(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace,
      )

      class Sleeping(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace,
      )

      class Eating(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.05,
      )

      class Educating(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace, formSocialConnectionsPerHour = 0.1,
      )

      class InThePark(hoursLeft: Double, targetPlace: Park) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.1,
      )

      class AtTheClub(hoursLeft: Double, targetPlace: Club) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 1.2,
      )

      class AtTheGym(hoursLeft: Double, targetPlace: Gym) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.05,
      )

      class WatchTv(hoursLeft: Double, targetPlace: Home) : DurationalState(
        hoursLeft,
        targetPlace,
      )
    }

    class Commuting(val direction: Direction) : State() {
      override fun toString(): String = "Commuting..."
    }

    companion object {
      val allStates = listOf(
        DurationalState.Working::class,
        DurationalState.JobHunt::class,
        DurationalState.Sleeping::class,
        DurationalState.Eating::class,
        DurationalState.Educating::class,
        Commuting::class,
        DurationalState.InThePark::class,
        DurationalState.AtTheClub::class,
        DurationalState.AtTheGym::class,
        DurationalState.WatchTv::class
      )
      val colors = mapOf(
        DurationalState.Working::class to Color(0xAB4e4553),
        DurationalState.JobHunt::class to Color(0xAB2e2523),
        DurationalState.Sleeping::class to Color(0xAB94bdf8),
        DurationalState.Eating::class to Color(0xAB108e5e),
        DurationalState.Educating::class to Color(0xABf8e394),
        Commuting::class to Color(0xAB302137),
        DurationalState.InThePark::class to Color(0xAB94f8b5),
        DurationalState.AtTheClub::class to Color(0xABf8b594),
        DurationalState.AtTheGym::class to Color(0xAB94f8e3),
        DurationalState.WatchTv::class to Color(0x882739f0),
      )
    }
  }

  companion object {
    private val firstNames = mapOf(
      Gender.Male to listOf(
        "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles", "Daniel", "Matthew", "Anthony",
        "Donald", "Mark", "Paul", "Steven", "Andrew", "Kenneth", "Joshua", "George", "Kevin", "Brian", "Edward", "Ronald", "Timothy"
      ),
      Gender.Female to listOf(
        "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen", "Nancy", "Lisa", "Betty",
        "Dorothy", "Sandra", "Ashley", "Kimberly", "Donna", "Emily", "Michelle", "Carol", "Amanda", "Melissa", "Deborah", "Stephanie",
      ),
      Gender.Other to listOf(
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
      val gender = when (random.nextFloat()) {
        in 0.0..0.48 -> Gender.Male
        in 0.48..0.95 -> Gender.Female
        else -> Gender.Other
      }
      return Actor(
        random,
        name = "${firstNames[gender]!!.random(random)} ${lastNames.random(random)}",
        gender = gender,
        needs = Needs.default(),
        yearsOfEducation = if (age <= 6) 0.0 else random.nextDouble(0.0, age - 5.0), // TODO better education system
        age = age.toDouble(),
        currentPosition = home.position,
        home = home,
        money = 100.0 * age
      )
    }
  }
}

enum class Gender {
  Male, Female, Other;
}

enum class AgeCategory(val startAge: Double, val endAge: Double) {
  CHILD(startAge = 0.0, endAge = 18.0),
  ADULT(startAge = 18.0, endAge = 70.0),
  SENIOR(startAge = 70.0, endAge = 100.00)
}

enum class WorkingCategory(val chartName: String) {
  CHILD("Child"),
  EMPLOYED("Employed"),
  UNEMPLOYED("Unemployed"),
  RETIRED("Retired");

  companion object {
    val colors = mapOf(
      CHILD to Color(0xFFf8e394),
      EMPLOYED to Color(0xFF108e5e),
      UNEMPLOYED to Color(0xFF2e2523),
      RETIRED to Color(0xFF94bdf8),
    )
  }
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
