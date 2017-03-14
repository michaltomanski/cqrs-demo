package com.mtomanski.timer.infrastructure.play.controller

import javax.inject.Inject

import com.mtomanski.timer.api.SpeedcuberQueryApi

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

class ViewsController @Inject()(queryApi: SpeedcuberQueryApi) extends Controller {

  def renderTimer = Action {
    Ok(com.mtomanski.timer.view.html.timer.render())
  }

  def bestAverages = Action.async {
    queryApi.getAllBestAvg().map { r =>
      Ok(com.mtomanski.timer.view.html.averages(r))
    }
  }

}