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

private fun Game.buildSocialConnections(actors: List<Actor>, elapsedHours: Double) {
  actors.forEach { actor ->
    if (actor.perceivedState.socializeFactor > Random.nextDouble() * elapsedHours) {
      val randomOtherActor = actors.random().takeIf { it != actor } ?: return@forEach
      actor.socializeWith(randomOtherActor)
      randomOtherActor.socializeWith(actor)
    }
  }
}

private fun Actor.socializeWith(other: Actor) {
  this.socialConnections.connections[other] = (this.socialConnections.connections[other] ?: 0.0) + 1
}
