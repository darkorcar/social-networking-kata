package com.social.util

import java.time.{Clock, LocalDateTime}

trait ClockProvider {

  protected val clock: Clock = Clock.systemDefaultZone()

  def now: Long = clock.millis()

}
