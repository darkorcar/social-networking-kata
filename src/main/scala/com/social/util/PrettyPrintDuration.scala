package com.social.util

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

trait PrettyPrintDuration {

  def prettyPrint(millis: Long): String = {
    val secs = millis / 1000
    val mins = (millis / 1000) / 60
    val hours = (millis / 1000) / (60 * 60)
    val days = ((millis / 1000) / (60 * 60)) / 24

    if (days > 0) s"${Duration.create(days, TimeUnit.DAYS).toString()}"
    else if (hours > 0) s"${Duration.create(hours, TimeUnit.HOURS).toString()}"
    else if (mins > 0) s"${Duration.create(mins, TimeUnit.MINUTES).toString()}"
    else if (secs > 0) s"${Duration.create(secs, TimeUnit.SECONDS).toString()}"
    else if (millis > 0) "1 second"
    else "0 seconds"
  }

}
