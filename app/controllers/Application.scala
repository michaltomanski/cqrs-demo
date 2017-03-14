package controllers


import javax.inject.Inject

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._

class Application @Inject()(actorSystem: ActorSystem, repo: BestRepo) extends Controller {

  implicit val timeout: Timeout = 3 seconds

  val actor = new SpeedcuberClusterShardLocator(actorSystem).speedcuber

  def index = Action.async(parse.json) { request =>
    request.body.validate[TimeDTO].fold(
      err => Future.successful(BadRequest(JsError(err).toString)),
      time => {
        (actor ? time).mapTo[TimeAdded].map(timeAdded =>
          Ok(Json.toJson(timeAdded.millis)))
      }
    )
  }

  def get = Action.async {
    repo.getAll().map { r =>
      Ok(views.html.best(r))
    }
  }

  def i = Action {
    Ok(views.html.index.render())
  }


}

case class BestAvg(user: String, millis: Long)
