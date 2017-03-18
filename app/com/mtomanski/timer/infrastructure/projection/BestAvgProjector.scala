package com.mtomanski.timer.infrastructure.projection

import javax.inject.Inject

import akka.Done
import akka.actor.Props
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.model.Speedcuber.BestAvgChanged
import com.mtomanski.timer.infrastructure.projection.builder.BestAvgViewBuilder
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BestAvgProjector @Inject()(viewBuilder: BestAvgViewBuilder) extends PersistentActor {
  private val logger = LoggerFactory.getLogger(getClass)
  private implicit val actorMaterializer = ActorMaterializer()(context)

  var state = State(offset = 0, firstOffsetSaved = false)

  private def handleEvent: PartialFunction[EventEnvelope, Future[Long]] = {
    case EventEnvelope(offset, _, _, event: BestAvgChanged) =>
      logger.info(s"Updating view of best averages with $event")
      viewBuilder.upsertBestAvgView(BestAvg(event.user, event.millis)).map(_ => offset)
    case EventEnvelope(offset, _, _, event) =>
      logger.debug(s"Ignoring $event during projection")
      Future.successful(offset)
  }

  override def receiveCommand: Receive = {
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
    source.mapAsync(1)(handleEvent).runWith(Sink.foreach(offset => self ! SaveOffset(offset)))
    logger.info("Event stream processing started")
  }

  private def dropIfNeeded = if (state.firstOffsetSaved) 1 else 0

  val readJournal = PersistenceQuery(context.system).readJournalFor[CassandraReadJournal]("cassandra-query-journal")

  private def persistOffset(offset: Long) = {
    state = state.copy(offset = offset, firstOffsetSaved = true)
  }

}

object BestAvgProjector {
  def props(viewBuilder: BestAvgViewBuilder) = Props(new BestAvgProjector(viewBuilder))
}

case class State(offset: Long, firstOffsetSaved: Boolean)
case class SaveOffset(offset: Long)
case class OffsetSaved(offset: Long)