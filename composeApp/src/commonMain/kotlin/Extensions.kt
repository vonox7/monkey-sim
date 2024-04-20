import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.display(decimals: Int = 2): String {
  require(decimals != 0) { "Use Math round to get an Int" }
  val factor = 10.toDouble().pow(decimals.toDouble())
  return ((this * factor).roundToLong().toDouble() / factor).toString()
}