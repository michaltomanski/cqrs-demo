package com.mtomanski.timer.infrastructure.play.controller

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.util.Timeout
import com.mtomanski.timer.api.SpeedcuberApi
import com.mtomanski.timer.domain.Speedcuber.{AddTime, TimeAdded}
import com.mtomanski.timer.infrastructure.akka.SpeedcuberClusterShardLocator
import com.mtomanski.timer.infrastructure.play.dto.TimeDTO
import controllers.BestRepo
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class TimerController @Inject()(api: SpeedcuberApi, repo: BestRepo) extends Controller {

  def index = Action.async(parse.json) { request =>
    request.body.validate[TimeDTO].fold(
      err => Future.successful(BadRequest(JsError(err).toString)),
      time => {
        api.addTime(AddTime(time.user, time.millis)).map(timeAdded =>
          Ok(Json.toJson(timeAdded.millis)))
      }
    )
  }

  def get = Action.async {
    repo.getAll().map { r =>
      Ok(views.html.averages(r))
    }
  }

  def i = Action {
    Ok(views.html.timer.render())
  }


}
