package com.mtomanski.timer.infrastructure.projection

import javax.inject.Inject

import akka.actor.Props
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.model.Speedcuber.BestAvgChanged
import com.mtomanski.timer.domain.repository.BestAvgRepository
import org.slf4j.LoggerFactory

class Projector @Inject()(repo: BestAvgRepository) extends PersistentActor {
  private val logger = LoggerFactory.getLogger(getClass)
  private implicit val actorMaterializer = ActorMaterializer()(context)

  var offset = 0L
  var firstOffsetSaved = false

  override def receiveCommand: Receive = {
    case SaveOffset(value) => persist(OffsetSaved(value)) { e =>
      offset = e.offset
      firstOffsetSaved = true
    }
    case event: BestAvgChanged =>
      logger.info(s"Updating view of best averages with $event")
      repo.upsert(BestAvg(event.user, event.millis))
  }

  override def receiveRecover: Receive = {
    case OffsetSaved(value) =>
      offset = value
      firstOffsetSaved = true
    case RecoveryCompleted =>
      logger.debug(s"Offset recovery completed. Last offset: $offset")
      val source = readJournal.eventsByTag("Speedcuber", offset).drop(if (firstOffsetSaved) 1 else 0)
      source.map{e => self ! e.event; e}.runWith(Sink.foreach{ e => self ! SaveOffset(e.offset)})
      logger.info("Event stream processing started")
  }

  override def persistenceId: String = "projector"

  val readJournal = PersistenceQuery(context.system).readJournalFor[CassandraReadJournal]("cassandra-query-journal")

}

object Projector {
  def props(repo: BestAvgRepository) = Props(new Projector(repo))
}

case class SaveOffset(offset: Long)
case class OffsetSaved(offset: Long)