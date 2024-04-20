package definitions

class Actor(
  val name: String,
  val needs: Needs,

  val yearsOfEducation: Int,
  val age: Int,
  val sex: Sex,
  val socialConnections: SocialConnections,
  var currentPosition: Position,
  var home: Home,
  // TODO partner
)

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
