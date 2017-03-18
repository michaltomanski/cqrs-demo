package com.mtomanski.timer.domain.repository

import com.mtomanski.timer.infrastructure.repository.table.BestAvgTable.BestAvgRow
import scala.concurrent.Future

trait BestAvgRepository {

  def getAll(): Future[Seq[BestAvgRow]]

}
