package com.mtomanski.timer.domain.repository

import com.mtomanski.timer.domain.model.BestAvg

import scala.concurrent.Future

trait BestAvgRepository {

  def getAll(): Future[Seq[BestAvg]]

  def upsert(bestAvg: BestAvg): Future[Int]

}
