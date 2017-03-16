package com.mtomanski.timer.infrastructure.repository

import javax.inject.Inject

import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.repository.BestAvgRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// This should be changed to a proper slick table instead of plain sql
class PostgresBestAvgRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends BestAvgRepository with HasDatabaseConfigProvider[JdbcProfile]{

  def getAll(): Future[Seq[BestAvg]] = {
    val q = sql"SELECT * FROM best_average ORDER BY average ASC LIMIT 10".as[(String, Long)]
    db.run(q).map(
      _.map(e => BestAvg(e._1, e._2))
    )
  }

  def upsert(bestAvg: BestAvg): Future[Int] = {
    val (user, millis) = (bestAvg.user, bestAvg.millis)
    db.run(for {
      exists <- checkIfExists(user)
      result <- if (exists) {
        update(user, millis)
      } else {
        insert(user, millis)
      }
    } yield result)
  }

  private def checkIfExists(user: String) = (sql"SELECT average FROM best_average WHERE username = $user".as[Long]).map(_.nonEmpty)

  private def insert(user: String, average: Long) = sqlu"INSERT INTO best_average VALUES ($user, $average)"

  private def update(user: String, average: Long) = sqlu"UPDATE best_average SET average = $average WHERE username = $user"

}
