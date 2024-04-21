package definitions

class SocialConnections {
  /** Map of actor to connection strength */
  val connections: MutableMap<Actor, Double> = mutableMapOf()
  var partner: Actor? = null
}

// Return false for the partner, as there it is not only potential but it must be true love
fun Actor.lovePotential(other: Actor): Boolean {
  return gender == other.preferences.partnerGenderPreference &&
      preferences.partnerGenderPreference == other.gender &&
      social.partner == null &&
      other.social.partner == null &&
      partnerAgePreference?.let { other.age.toInt() in it } == true &&
      other.partnerAgePreference?.let { age.toInt() in it } == true
}