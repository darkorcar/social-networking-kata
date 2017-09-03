package com.social

import java.time.{Clock, Instant, ZoneId}

trait FixedClock {

  def fixedClock(millis: Long): Clock = Clock.fixed(Instant.ofEpochMilli(millis), ZoneId.systemDefault())

}
