package com.mtomanski.timer.infrastructure.akka

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.mtomanski.timer.domain.{Speedcuber, SpeedcuberLocator}
import com.mtomanski.timer.domain.Speedcuber.AddTime

class SpeedcuberClusterShardLocator @Inject()(actorSystem: ActorSystem) extends SpeedcuberLocator {


  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg: AddTime => (msg.user, msg)
  }

  val numberOfShards = 10

  val extractShardId: ShardRegion.ExtractShardId = {
    case msg: AddTime => (msg.user.hashCode % numberOfShards).toString
  }


  val speedcuber: ActorRef = ClusterSharding(actorSystem).start(
    typeName = "Speedcuber",
    entityProps = Speedcuber.props(),
    settings = ClusterShardingSettings(actorSystem),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId
  )
}
