package controllers

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}

class Speedcuber extends PersistentActor {

  var times: Seq[Long] = Nil
  var bestAvg: Long = _

  override def receiveRecover: Receive = {
    case e: Event => updateState(e)
    case RecoveryCompleted => println("Recovery completed")
    case e => println(s"Unknown event ${e.toString}")
  }

  override def receiveCommand: Receive = {
    case time: TimeDTO => persist(TimeAdded(time.user, time.millis)) { event =>
      updateState(event)
      println(time.millis)
      val lastAvg = calculateLastAvg(times)
      println(s"last avg: $lastAvg")
      if (times.length == 5 || lastAvg < bestAvg) {
        println(s"NEW BEST! $lastAvg")
        persist(BestAvgChanged(event.user, lastAvg)) {
          event => updateState(event)
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
  println("========================== CREATING")
  def props() = Props(new Speedcuber)
}
