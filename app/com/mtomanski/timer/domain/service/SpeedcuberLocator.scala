package com.mtomanski.timer.domain.service

import akka.actor.ActorRef

trait SpeedcuberLocator {

  def speedcuber(): ActorRef

}
