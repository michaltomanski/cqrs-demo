package com.mtomanski.timer.domain

import akka.actor.ActorRef

trait SpeedcuberLocator {

  def speedcuber(): ActorRef

}
