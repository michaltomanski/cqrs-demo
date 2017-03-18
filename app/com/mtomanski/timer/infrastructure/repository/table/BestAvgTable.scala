package com.mtomanski.timer.infrastructure.repository.table

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

object BestAvgTable extends BestAvgTable

private[table] trait BestAvgTable {

  case class BestAvgRow(user: String, millis: Long)

  class BestAvgs(tag: Tag) extends Table[BestAvgRow](tag, "best_average") {
    def user = column[String]("username", O.PrimaryKey)
    def average = column[Long]("average")

    def * = (user, average) <> (BestAvgRow.tupled, BestAvgRow.unapply)

  }

  val bestAvgs = TableQuery[BestAvgs]

}
