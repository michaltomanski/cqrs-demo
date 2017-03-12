import javax.inject.{Inject, Provider}

import akka.actor.{ActorRef, ActorSystem, DeadLetter}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import controllers.{BestRepo, Projector}
import play.api.{Configuration, Environment}
import play.api.libs.concurrent.AkkaGuiceSupport

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    //bind(classOf[CassandraReadJournal]).toProvider(classOf[CassandraReadJournalProvider])
    //bindActor[Projector]("view-projector-dd")
    bind(classOf[ActorRef])
      .annotatedWith(Names.named("view-projector"))
      .toProvider(classOf[ProjectorProvider])
      .asEagerSingleton()

  }


}


class ProjectorProvider @Inject()(actorSystem: ActorSystem, repo: BestRepo) extends Provider[ActorRef] {
  private lazy val instance = actorSystem.actorOf(Projector.props(repo))

  override def get(): ActorRef = instance
}

class CassandraReadJournalProvider @Inject()(actorSystem: ActorSystem) extends Provider[CassandraReadJournal] {
  private lazy val instance =
    PersistenceQuery(actorSystem).readJournalFor[CassandraReadJournal]("cassandra-query-journal")

  override def get(): CassandraReadJournal = instance
}
