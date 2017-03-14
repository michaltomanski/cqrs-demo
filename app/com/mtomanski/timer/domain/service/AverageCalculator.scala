package com.mtomanski.timer.domain.service

object AverageCalculator {

  def calculateLastAvg5(times: Seq[Long]): Long = {
    if (times.length >= 5) {
      val last5 = times.takeRight(5)
      (last5.sum - last5.max - last5.min ) / 3
    } else {
      0
    }
  }

}
