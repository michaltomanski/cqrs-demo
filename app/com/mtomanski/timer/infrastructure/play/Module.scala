package com.mtomanski.timer.infrastructure.play

import javax.inject.{Inject, Provider}

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.mtomanski.timer.domain.SpeedcuberLocator
import com.mtomanski.timer.infrastructure.akka.SpeedcuberClusterShardLocator
import controllers.{BestRepo, Projector}
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ActorRef])
      .annotatedWith(Names.named("view-projector"))
      .toProvider(classOf[ProjectorProvider])
      .asEagerSingleton()
    bind(classOf[SpeedcuberLocator]).to(classOf[SpeedcuberClusterShardLocator])

  }
}

class ProjectorProvider @Inject()(actorSystem: ActorSystem, repo: BestRepo) extends Provider[ActorRef] {
  private lazy val instance = actorSystem.actorOf(Projector.props(repo))

  override def get(): ActorRef = instance
}
