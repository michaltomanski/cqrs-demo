package controllers

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

class SpeedcuberClusterShardLocator @Inject()(actorSystem: ActorSystem) {


  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg: TimeDTO => (msg.user, msg)
  }

  val numberOfShards = 10

  val extractShardId: ShardRegion.ExtractShardId = {
    case msg: TimeDTO => (msg.user.hashCode % numberOfShards).toString
  }


  val speedcuber: ActorRef = ClusterSharding(actorSystem).start(
    typeName = "Speedcuber",
    entityProps = Speedcuber.props(),
    settings = ClusterShardingSettings(actorSystem),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId
  )
}
