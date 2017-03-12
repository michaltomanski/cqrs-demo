package controllers

import javax.inject.Inject

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

class Projector @Inject()(repo: BestRepo) extends PersistentActor {
  private implicit val actorMaterializer = ActorMaterializer()(context)

  var offset = 0L
  var firstOffsetSaved = false

  override def receiveCommand: Receive = {
    case SaveOffset(value) => persist(OffsetSaved(value)) { e =>
      offset = e.offset
      firstOffsetSaved = true
    }
    case event: BestAvgChanged =>
      println(s"Updating view of best averages with $event")
      repo.upsert(event.user, event.millis)
  }

  override def receiveRecover: Receive = {
    case OffsetSaved(value) =>
      offset = value
      firstOffsetSaved = true
    case RecoveryCompleted =>
      println("Offset recovery completed")
      val source = readJournal.eventsByTag("all", offset).drop(if (firstOffsetSaved) 1 else 0)
      source.map{e => self ! e.event; e}.runWith(Sink.foreach{ e => self ! SaveOffset(e.offset)})
      println("Stream started")
  }

  override def persistenceId: String = "projector"

  val readJournal = PersistenceQuery(context.system).readJournalFor[CassandraReadJournal]("cassandra-query-journal")

}

object Projector {
  def props(repo: BestRepo) = Props(new Projector(repo))
}

case class SaveOffset(offset: Long)
case class OffsetSaved(offset: Long)
case object StreamCompleted