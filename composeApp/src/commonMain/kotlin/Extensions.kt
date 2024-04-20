import kotlin.math.pow

fun Double.display(decimals: Int = 2): String = ((this * 10.0.pow(decimals)).toInt() / 10.0.pow(decimals)).toString()