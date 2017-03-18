package com.mtomanski.timer.api

import javax.inject.Inject

import com.mtomanski.timer.domain.model.BestAvg
import com.mtomanski.timer.domain.repository.BestAvgRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
class SpeedcuberQueryApi @Inject()(bestAvgRepo: BestAvgRepository) {

  def getAllBestAvg(): Future[Seq[BestAvg]] = bestAvgRepo.getAll()
    .map(_.map(row => BestAvg(row.user, row.millis)))

}
