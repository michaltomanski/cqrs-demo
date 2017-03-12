package controllers


import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

class Application @Inject()(actorSystem: ActorSystem, repo: BestRepo) extends Controller {

  val actor = new SpeedcuberClusterShardLocator(actorSystem).speedcuber

  def index = Action(parse.json) { request =>
    println(request.body)
    request.body.validate[TimeDTO].fold(
      err => BadRequest(JsError(err).toString),
      time => {
        actor ! time
        Ok(Json.toJson(time.millis))
      }
    )
  }

  def get = Action.async {
    implicit val format = Json.format[BestAvg] // fix
    repo.getAll().map { r =>
      Ok(Json.toJson(r))
    }
  }

  def i = Action {
    Ok(views.html.index.render())
  }


}

case class BestAvg(user: String, millis: Long)

object BestAvg {
  implicit val format = Json.format[BestAvg]
}

