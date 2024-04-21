package game

import definitions.Actor
import kotlin.random.Random

fun Game.decaySocialConnections(elapsedHours: Double) {
  this.world.actors.forEach { it.decaySocialConnections(elapsedHours) }
}

private fun Actor.decaySocialConnections(elapsedHours: Double) {
  // 10% chance to forget friends.
  // Needed to prevent exhaustive performance hit, as we don't want to iterate each tick over all connections.
  if (Random.nextDouble() > elapsedHours * 10) return

  this.social.connections
    .forEach { (actor, connection) ->
      if (connection > 0) {
        this.social.connections[actor] = connection - elapsedHours * 0.1
      }
    }
  this.social.connections.entries.removeAll { it.value <= 0 }

  if (this.social.connections.count() > 30) {
    // Remove the weakest connection, we only know 30 people
    this.social.connections.remove(this.social.connections.minByOrNull { it.value }!!.key)
  }
}