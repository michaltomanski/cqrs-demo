package com.mtomanski.timer.domain.model

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.mtomanski.timer.domain.model.Speedcuber._
import com.mtomanski.timer.domain.service.AverageCalculator

object Speedcuber {

  sealed trait Command

  case class AddTime(user: String, millis: Long) extends Command

  sealed trait Event

  case class TimeAdded(user: String, millis: Long) extends Event

  case class BestAvgChanged(user: String, millis: Long) extends Event

  private final case class State(times: Seq[Long] = Nil, bestAvg: Long = 0)

  def props() = Props(new Speedcuber)
}

class Speedcuber extends PersistentActor {

  private var state = State()

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case RecoveryCompleted => println("Events recovery completed")
  }

  override def receiveCommand: Receive = {
    case addTime: AddTime => persist(TimeAdded(addTime.user, addTime.millis)) { event =>
      updateState(event)
      println(s"New time persisted $event")
      val newAvg = AverageCalculator.calculateLastAvg5(state.times)
      if (isNewAvgBetter(newAvg)) {
        persist(BestAvgChanged(event.user, newAvg)) { event =>
          updateState(event)
          println(s"New best average persisted $event")
        }
      }
      sender ! event
    }
  }

  def updateState(event: Event) = event match {
    case timeAdded: TimeAdded =>
      val timesUpdated = state.times :+ timeAdded.millis
      state = state.copy(times = timesUpdated)
    case bestAvgChanged: BestAvgChanged =>
      val newBestAvg = bestAvgChanged.millis
      state = state.copy(bestAvg = newBestAvg)
  }

  private def isNewAvgBetter(newAvg: Long) = state.times.length == 5 || newAvg < state.bestAvg

  override def persistenceId: String = "speedcuber" + self.path.name
}


