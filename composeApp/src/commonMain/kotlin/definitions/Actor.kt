package definitions

class Actor(
  val name: String,
  val needs: Set<Need>,

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
