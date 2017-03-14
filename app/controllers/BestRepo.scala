package controllers

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class BestRepo @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  def getAll() = {
    val q = sql"SELECT * FROM best_average ORDER BY average ASC LIMIT 10".as[(String, Long)]
    db.run(q).map(
      _.map(e => BestAvg(e._1, e._2))
    )
  }

  def upsert(user: String, average: Long) = {
    db.run(for {
      e <- exists(user)
      _ <- e match {
        case true => update(user, average)
        case false => insert(user, average)
      }
    } yield 1)
  }

  private def exists(user: String) = (sql"SELECT average FROM best_average WHERE username = $user".as[Long]).map(_.nonEmpty)

  private def insert(user: String, average: Long) = sqlu"INSERT INTO best_average VALUES ($user, $average)"

  private def update(user: String, average: Long) = sqlu"UPDATE best_average SET average = $average WHERE username = $user"

}
