package definitions

class SocialConnections {
  /** Map of actor to connection strength */
  val connections: MutableMap<Actor, Double> = mutableMapOf()
  var partner: Actor? = null
}