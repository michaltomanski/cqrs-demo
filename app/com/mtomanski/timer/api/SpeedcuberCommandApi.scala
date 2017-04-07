package com.mtomanski.timer.api

import javax.inject.Inject

import com.mtomanski.timer.domain.model.Speedcuber.{AddTime, TimeAdded}
import akka.pattern.ask
import akka.util.Timeout
import com.mtomanski.timer.domain.service.SpeedcuberLocator
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.duration._

class SpeedcuberCommandApi @Inject()(speedcuberLocator: SpeedcuberLocator) {

  implicit val timeout: Timeout = 3.seconds
  private val logger = LoggerFactory.getLogger(getClass)

  def addTime(command: AddTime): Future[TimeAdded] = {
    logger.info(s"Handling $command command")
    (speedcuberLocator.speedcuber() ? command).mapTo[TimeAdded]
  }

}
