package definitions

import androidx.compose.ui.graphics.Color
import definitions.WorkingCategory.*
import kotlin.random.Random

class Actor(
  random: Random,
  val firstName: String,
  var lastName: String,
  val needs: Needs,

  var yearsOfEducation: Double,
  var age: Double, // In years
  val gender: Gender,
  var money: Double,

  var currentPosition: Position, // Needs to be manually synchronized with World.actorsGroupedByPosition
  var home: Home, // Needs to be manually synchronized with Home.residents
  var workPlace: Place? = null, // Needs to be manually synchronized with Place.Work.currentWorkingPeople
) {
  val name: String get() = "$firstName $lastName"

  val partnerAgePreference: ClosedRange<Double>?
    get() {
      if (age <= 18) return null
      // XKCD Dating Pools https://xkcd.com/314/
      val minAge = (age / 2.0 + 7).coerceAtLeast(18.0)
      val maxAge = (age + (age - 10) * 0.7).coerceIn(minAge, 100.0)
      return minAge..maxAge
    }

  var alive = true
  val social: SocialConnections = SocialConnections()

  val preferences = Preferences(gender, random)

  val isReproductive: Boolean get() = if (gender == Gender.Male) age in 18.0..50.0 else age in 18.0..35.0

  val perceivedState: State
    get() {
      return if (currentPosition == targetState.targetPlace.position) {
        targetState
      } else {
        State.Commuting
      }
    }

  var targetState: State.DurationalState = State.DurationalState.Sleeping(0.0, home)

  val workingCategory
    get(): WorkingCategory = when (age) {
      in AgeCategory.TODDLER.range -> TODDLER
      in AgeCategory.CHILD.range -> CHILD
      in AgeCategory.ADULT.range -> if (workPlace != null) EMPLOYED else UNEMPLOYED
      in AgeCategory.SENIOR.range -> RETIRED
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
        formSocialConnectionsPerHour = 0.1,
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
        formSocialConnectionsPerHour = 0.1,
      )

      class Educating(hoursLeft: Double, targetPlace: Place) : DurationalState(
        hoursLeft,
        targetPlace, formSocialConnectionsPerHour = 0.1,
      )

      class InThePark(hoursLeft: Double, targetPlace: Park) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.3,
      )

      class AtTheClub(hoursLeft: Double, targetPlace: Club) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 5.0,
      )

      class AtTheGym(hoursLeft: Double, targetPlace: Gym) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.1,
      )

      class WatchTv(hoursLeft: Double, targetPlace: Home) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 0.01,
      )

      class VisitFriend(hoursLeft: Double, targetPlace: Home) : DurationalState(
        hoursLeft,
        targetPlace,
        formSocialConnectionsPerHour = 10.0,
      )
    }

    object Commuting : State() {
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
        DurationalState.WatchTv::class,
        DurationalState.VisitFriend::class,
      )
      val colors = mapOf(
        DurationalState.Working::class to Color(0xAB4e4553),
        DurationalState.JobHunt::class to Color(0xAB825d56),
        DurationalState.Sleeping::class to Color(0xAB94bdf8),
        DurationalState.Eating::class to Color(0xAB108e5e),
        DurationalState.Educating::class to Color(0xABf8e394),
        Commuting::class to Color(0xAB302137),
        DurationalState.InThePark::class to Color(0xAB68b35b),
        DurationalState.AtTheClub::class to Color(0xABf8b594),
        DurationalState.AtTheGym::class to Color(0xAB6e45a8),
        DurationalState.WatchTv::class to Color(0x882739f0),
        DurationalState.VisitFriend::class to Color(0xABde2f7b),
      )
    }
  }

  companion object {
    fun create(random: Random, home: Home, lastNameOverride: String? = null, ageOverride: Int? = null): Actor {
      val age = ageOverride ?: when (random.nextInt(0, 5)) {
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
        firstName = ActorNames.firstNames[gender]!!.random(random),
        lastName = lastNameOverride ?: ActorNames.lastNames.random(random),
        gender = gender,
        needs = Needs.default(),
        yearsOfEducation = if (age <= 6) 0.0 else random.nextDouble(0.0, age - 5.0),
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

enum class AgeCategory(private val startAge: Double, private val endAge: Double) {
  TODDLER(startAge = 0.0, endAge = 6.0),
  CHILD(startAge = 6.0, endAge = 18.0),
  ADULT(startAge = 18.0, endAge = 70.0),
  SENIOR(startAge = 70.0, endAge = Double.MAX_VALUE);

  val range: OpenEndRange<Double> = startAge..<endAge
}

enum class WorkingCategory(val chartName: String) {
  TODDLER("Toddler"),
  CHILD("Child"),
  EMPLOYED("Employed"),
  UNEMPLOYED("Unemployed"),
  RETIRED("Retired");

  companion object {
    val colors = mapOf(
      TODDLER to Color(0xAB94bdf8),
      CHILD to Color(0xABf8e394),
      EMPLOYED to Color(0xAB4e4553),
      UNEMPLOYED to Color(0xAB825d56),
      RETIRED to Color(0xAB68b35b),
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
