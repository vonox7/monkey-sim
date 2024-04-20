package game

import definitions.Actor

fun Game.decaySocialConnections(elapsedHours: Double) {
  this.world.actors.forEach { it.decaySocialConnections(elapsedHours) }
}

private fun Actor.decaySocialConnections(elapsedHours: Double) {
  this.socialConnections.connections
    .filter { it.value > 0 }
    .forEach { (actor, connection) ->
      this.socialConnections.connections[actor] = connection - elapsedHours * 0.01
    }
  this.socialConnections.connections.entries.removeAll { it.value <= 0 }

  if (this.socialConnections.connections.count() > 30) {
    // Remove the weakest connection, we only know 30 people
    this.socialConnections.connections.remove(this.socialConnections.connections.minByOrNull { it.value }!!.key)
  }
}