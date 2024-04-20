package game

import definitions.Actor
import kotlin.random.Random


fun Game.buildSocialConnections(elapsedHours: Double) {
  this.world.actors
    .groupBy { it.currentPosition.x to it.currentPosition.y }
    .forEach { (_, actors) ->
      val socializableActors = actors.filter { it.perceivedState.socializeFactor > 0.0 }
      if (socializableActors.count() > 1) {
        buildSocialConnections(socializableActors, elapsedHours)
      }
    }
}


private fun buildSocialConnections(actors: List<Actor>, elapsedHours: Double) {
  actors.forEach { actor ->
    if (actor.perceivedState.socializeFactor > Random.nextDouble() * elapsedHours) {
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

  if (connection != null && connection > datingThreshold) {
    social.connections.remove(other)
    social.connections.remove(this)
    social.partner = other
  }
}
