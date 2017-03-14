package com.mtomanski.timer.api

import javax.inject.Inject

import com.mtomanski.timer.domain.Speedcuber.{AddTime, TimeAdded}
import com.mtomanski.timer.domain.SpeedcuberLocator

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

class SpeedcuberApi @Inject()(speedcuberLocator: SpeedcuberLocator) {

  implicit val timeout: Timeout = 3.seconds

  def addTime(command: AddTime) = {
    (speedcuberLocator.speedcuber() ? command).mapTo[TimeAdded]
  }


}
