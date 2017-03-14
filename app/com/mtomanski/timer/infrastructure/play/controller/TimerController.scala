package com.mtomanski.timer.infrastructure.play.controller

import javax.inject.Inject

import com.mtomanski.timer.api.SpeedcuberCommandApi
import com.mtomanski.timer.domain.model.Speedcuber.AddTime
import com.mtomanski.timer.infrastructure.play.dto.TimeDTO
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class TimerController @Inject()(commandApi: SpeedcuberCommandApi) extends Controller {

  def addTime = Action.async(parse.json) { request =>
    request.body.validate[TimeDTO].fold(
      err => Future.successful(BadRequest(JsError(err).toString)),
      time => {
        commandApi.addTime(AddTime(time.user, time.millis)).map(timeAdded =>
          Ok(Json.toJson(timeAdded.millis)))
      }
    )
  }

}
