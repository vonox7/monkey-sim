package game

import definitions.Actor
import definitions.lovePotential
import kotlin.random.Random

fun Game.buildSocialConnections(elapsedHours: Double) {
  this.world.actors
    .groupBy { it.currentPosition }
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
      // Partners (and family house members) do not get a social connection, they are treated differently.
      val randomOtherActor = actors.random().takeIf { it != actor && it.home != actor.home } ?: return@forEach

      actor.socializeWith(randomOtherActor)
      randomOtherActor.socializeWith(actor)

      // We socialized with max 1 person, that's enough for this tick.
      // This favours actors that are at the beginning of the actor list more - but that's fine.
      return@buildSocialConnections
    }
  }
}

private const val datingThreshold = 10
private fun Actor.socializeWith(other: Actor) {
  val existingConnection = social.connections[other] ?: 0.0
  // Build connections up to datingThreshold faster, then slower to not have 1 connection that is too strong and prevents from being social
  val newConnection = existingConnection + (if (existingConnection > 10) 0.1 else 1.0)
  social.connections[other] = newConnection

  if (newConnection > datingThreshold && this.lovePotential(other)) {
    social.connections.remove(other)
    social.connections.remove(this)

    social.partner = other
    other.social.partner = this

    // We found a partner (which doesn't count into the social connection sum), so we don't need so much other connections any more
    preferences.minConnectionStrengthSum /= 2
    other.preferences.minConnectionStrengthSum /= 2

    // Move to the place with the higher income
    if (other.money > this.money) {
      this.home.residents.remove(other)
      this.home = other.home
      other.home.residents.add(this)
    } else {
      other.home.residents.remove(this)
      other.home = this.home
      this.home.residents.add(other)
    }

    val newLastName = if (other.age > age) other.lastName else lastName
    println("LOVE!!! $this and $other are now partners and are now the $newLastName family")

    lastName = newLastName
    other.lastName = newLastName
  }
}
