package com.mtomanski.timer.infrastructure.projection.builder

import javax.inject.Inject

import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.infrastructure.repository.table.BestAvgTable.{BestAvgRow, bestAvgs}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class PostgresBestAvgViewBuilder @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends BestAvgViewBuilder with HasDatabaseConfigProvider[JdbcProfile] {

  def upsertBestAvgView(bestAvg: BestAvg): Future[Int] =
    db.run(bestAvgs.insertOrUpdate(BestAvgRow(bestAvg.user, bestAvg.millis)))

}
