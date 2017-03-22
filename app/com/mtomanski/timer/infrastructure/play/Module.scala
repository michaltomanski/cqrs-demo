package com.mtomanski.timer.infrastructure.play

import javax.inject.{Inject, Provider}

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.cluster.singleton.{
  ClusterSingletonManager,
  ClusterSingletonManagerSettings,
  ClusterSingletonProxy,
  ClusterSingletonProxySettings
}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.mtomanski.timer.domain.repository.BestAvgRepository
import com.mtomanski.timer.domain.service.SpeedcuberLocator
import com.mtomanski.timer.infrastructure.locator.SpeedcuberClusterShardLocator
import com.mtomanski.timer.infrastructure.projection.BestAvgProjector
import com.mtomanski.timer.infrastructure.projection.builder.{
  BestAvgViewBuilder,
  PostgresBestAvgViewBuilder
}
import com.mtomanski.timer.infrastructure.repository.PostgresBestAvgRepository
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ActorRef])
      .annotatedWith(Names.named("view-projector"))
      .toProvider(classOf[ProjectorProvider])
      .asEagerSingleton()
    bind(classOf[SpeedcuberLocator]).to(classOf[SpeedcuberClusterShardLocator])
    bind(classOf[BestAvgRepository]).to(classOf[PostgresBestAvgRepository])
    bind(classOf[BestAvgViewBuilder]).to(classOf[PostgresBestAvgViewBuilder])
  }
}

class ProjectorProvider @Inject()(actorSystem: ActorSystem,
                                  viewBuilder: BestAvgViewBuilder)
    extends Provider[ActorRef] {

  val clustrerSingletonManager = actorSystem.actorOf(
    ClusterSingletonManager.props(
      singletonProps = BestAvgProjector.props(viewBuilder),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(actorSystem)), "proj")

  println(clustrerSingletonManager.path.toStringWithoutAddress)
  println(actorSystem.name)

  val proxy = ClusterSingletonProxy.props(
    "/user/proj",
    ClusterSingletonProxySettings(actorSystem))

  private lazy val instance =
    actorSystem.actorOf(proxy)

  override def get(): ActorRef = instance
}
