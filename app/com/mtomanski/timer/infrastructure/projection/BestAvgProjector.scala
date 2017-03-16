package com.mtomanski.timer.infrastructure.projection

import javax.inject.Inject

import akka.actor.Props
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.model.Speedcuber.BestAvgChanged
import com.mtomanski.timer.domain.repository.BestAvgRepository
import org.slf4j.LoggerFactory

class BestAvgProjector @Inject()(repo: BestAvgRepository) extends PersistentActor {
  private val logger = LoggerFactory.getLogger(getClass)
  private implicit val actorMaterializer = ActorMaterializer()(context)

  var state = State(offset = 0, firstOffsetSaved = false)

  override def receiveCommand: Receive = {
    case event: BestAvgChanged =>
      logger.info(s"Updating view of best averages with $event")
      repo.upsert(BestAvg(event.user, event.millis))
    case SaveOffset(value) => persist(OffsetSaved(value)) { offsetSaved =>
      persistOffset(offsetSaved.offset)
    }
  }

  override def receiveRecover: Receive = {
    case OffsetSaved(offset) =>
      persistOffset(offset)
    case RecoveryCompleted =>
      logger.debug(s"Offset recovery completed. Last offset: ${state.offset}")
      startProcessing()
  }

  override def persistenceId: String = "projector"

  private def startProcessing() = {
    val source = readJournal.eventsByTag("Speedcuber", state.offset).drop(dropIfNeeded)
    source.map{enveloped => self ! enveloped.event; enveloped}
      .runWith(Sink.foreach{ enveloped => self ! SaveOffset(enveloped.offset)})
    logger.info("Event stream processing started")
  }

  private def dropIfNeeded = if (state.firstOffsetSaved) 1 else 0

  val readJournal = PersistenceQuery(context.system).readJournalFor[CassandraReadJournal]("cassandra-query-journal")

  private def persistOffset(offset: Long) = {
    state = state.copy(offset = offset, firstOffsetSaved = true)
  }

}

object BestAvgProjector {
  def props(repo: BestAvgRepository) = Props(new BestAvgProjector(repo))
}

case class State(offset: Long, firstOffsetSaved: Boolean)
case class SaveOffset(offset: Long)
case class OffsetSaved(offset: Long)