package controllers

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}

class Speedcuber extends PersistentActor {

  var times: Seq[Long] = Nil
  var bestAvg: Long = 0

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case RecoveryCompleted => println("Events recovery completed")
  }

  override def receiveCommand: Receive = {
    case time: TimeDTO => persist(TimeAdded(time.user, time.millis)) { event =>
      updateState(event)
      println(s"New time persisted $event")
      val lastAvg = calculateLastAvg(times)
      if (times.length == 5 || lastAvg < bestAvg) {
        persist(BestAvgChanged(event.user, lastAvg)) { event =>
          updateState(event)
          println("New best average persisted $event")
        }
      }
      sender ! time
    }
  }

  private def calculateLastAvg(times: Seq[Long]): Long = {
    if (times.length >= 5) {
      val t = times.takeRight(5)
      (t.sum - t.max - t.min ) / 3
    } else {
      0
    }
  }

  private def updateState(event: Event) = event match {
    case timeAdded: TimeAdded => times = times :+ timeAdded.millis
    case bestAvgChanged: BestAvgChanged => bestAvg = bestAvgChanged.millis
  }

  override def persistenceId: String = "speedcuber" + self.path.name
}

sealed trait Event

case class TimeAdded(user: String, millis: Long) extends Event

case class BestAvgChanged(user: String, millis: Long) extends Event

object Speedcuber {
  def props() = Props(new Speedcuber)
}
