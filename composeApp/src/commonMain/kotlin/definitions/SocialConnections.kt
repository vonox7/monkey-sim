package definitions

class SocialConnections {
  /** Map of actor to connection strength */
  val connections: MutableMap<Actor, Double> = mutableMapOf()
  var partner: Actor? = null
}

fun Actor.lovePotential(other: Actor): Boolean {
  if (other == social.partner) return true

  if (gender == other.preferences.partnerGenderPreference &&
    preferences.partnerGenderPreference == other.gender &&
    social.partner == null &&
    other.social.partner == null
  ) {
    return true
  }

  return false
}