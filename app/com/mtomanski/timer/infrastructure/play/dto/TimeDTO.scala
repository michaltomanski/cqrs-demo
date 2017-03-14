package com.mtomanski.timer.infrastructure.play.dto

import play.api.libs.json.Json

case class TimeDTO(user: String, millis: Long)

object TimeDTO {
  implicit val format = Json.format[TimeDTO]
}