package game

import definitions.Actor
import definitions.lovePotential
import kotlin.random.Random


fun Game.buildSocialConnections(elapsedHours: Double) {
  this.world.actors
      .groupBy { it.currentPosition.x to it.currentPosition.y }
      .forEach { (_, actors) ->
        val socializableActors = actors.filter { it.perceivedState.formSocialConnectionsPerHour > 0.0 }
        if (socializableActors.count() > 1) {
          buildSocialConnections(socializableActors, elapsedHours)
        }
      }
}


private fun buildSocialConnections(actors: List<Actor>, elapsedHours: Double) {
  actors.forEach { actor ->
    if (Random.nextDouble() < actor.perceivedState.formSocialConnectionsPerHour * elapsedHours) {
      val randomOtherActor = actors.random().takeIf { it != actor } ?: return@forEach

      actor.socializeWith(randomOtherActor)
      randomOtherActor.socializeWith(actor)
    }
  }
}

private const val datingThreshold = 10
private fun Actor.socializeWith(other: Actor) {
  val connection = social.connections[other]
  social.connections[other] = (connection ?: 0.0) + 1

  if (connection != null && connection > datingThreshold && this.lovePotential(other)) {
    social.connections.remove(other)
    social.connections.remove(this)
    social.partner = other
    other.social.partner = this
    if (other.money > this.money) {
      this.home = other.home
    }
    println("LOVE!!! $this and $other are now partners")
  }
}
