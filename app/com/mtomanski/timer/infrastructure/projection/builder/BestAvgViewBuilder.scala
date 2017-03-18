package com.mtomanski.timer.infrastructure.projection.builder

import com.mtomanski.timer.domain.model.BestAvg

import scala.concurrent.Future

trait BestAvgViewBuilder {

  def upsertBestAvgView(bestAvg: BestAvg): Future[Int]

}
