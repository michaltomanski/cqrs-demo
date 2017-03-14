package com.mtomanski.timer.infrastructure.locator

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.mtomanski.timer.domain.model.Speedcuber
import com.mtomanski.timer.domain.model.Speedcuber.AddTime
import com.mtomanski.timer.domain.service.SpeedcuberLocator

class SpeedcuberClusterShardLocator @Inject()(actorSystem: ActorSystem) extends SpeedcuberLocator {

  val numberOfShards = 10

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg: AddTime => (msg.user, msg)
  }

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
