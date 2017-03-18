package com.mtomanski.timer.infrastructure.play

import javax.inject.{Inject, Provider}

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.mtomanski.timer.domain.repository.BestAvgRepository
import com.mtomanski.timer.domain.service.SpeedcuberLocator
import com.mtomanski.timer.infrastructure.locator.SpeedcuberClusterShardLocator
import com.mtomanski.timer.infrastructure.projection.BestAvgProjector
import com.mtomanski.timer.infrastructure.repository.PostgresBestAvgRepository
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ActorRef])
      .annotatedWith(Names.named("view-projector"))
      .toProvider(classOf[ProjectorProvider])
      .asEagerSingleton()
    bind(classOf[SpeedcuberLocator]).to(classOf[SpeedcuberClusterShardLocator])
    bind(classOf[BestAvgRepository]).to(classOf[PostgresBestAvgRepository])
  }
}

class ProjectorProvider @Inject()(actorSystem: ActorSystem, repo: BestAvgRepository) extends Provider[ActorRef] {
  private lazy val instance = actorSystem.actorOf(BestAvgProjector.props(repo))

  override def get(): ActorRef = instance
}
