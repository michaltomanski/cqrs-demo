package com.mtomanski.timer.infrastructure.repository

import javax.inject.Inject

import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.repository.BestAvgRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import com.mtomanski.timer.infrastructure.repository.table.BestAvgTable._

import scala.concurrent.Future

class PostgresBestAvgRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends BestAvgRepository with HasDatabaseConfigProvider[JdbcProfile] {

  def getAll(): Future[Seq[BestAvgRow]] =
    db.run(bestAvgs.sortBy(_.average.asc).take(10).result)

  def upsert(bestAvg: BestAvg): Future[Int] =
    db.run(bestAvgs.insertOrUpdate(BestAvgRow(bestAvg.user, bestAvg.millis)))

}


