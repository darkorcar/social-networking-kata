package com.social.util

import java.util.concurrent.TimeUnit.{DAYS, HOURS, MINUTES, SECONDS}

import com.social.BaseSpec

class PrettyPrintDurationTest extends BaseSpec with PrettyPrintDuration {

  "prettyPrint in seconds" should {

    "print '0 seconds' when duration less than 0" in {
      prettyPrint(-1) shouldEqual "0 seconds"
    }

    "print '1 second' when duration less than two seconds" in {
      prettyPrint(SECONDS.toMillis(1)-1) shouldEqual "1 second"
      prettyPrint(SECONDS.toMillis(1))   shouldEqual "1 second"
      prettyPrint(SECONDS.toMillis(2)-1) shouldEqual "1 second"
    }

    "print duration in seconds when greater then two second and less then one minute" in {
      prettyPrint(SECONDS.toMillis(2)) shouldEqual "2 seconds"
      prettyPrint(SECONDS.toMillis(35)) shouldEqual "35 seconds"
      prettyPrint(MINUTES.toMillis(1)-1) shouldEqual "59 seconds"
    }
  }

  "prettyPrint in minutes" should {

    "print duration in minute when greater then 60 seconds and less then two minutes" in {
      prettyPrint(MINUTES.toMillis(1)) shouldEqual "1 minute"
      prettyPrint(MINUTES.toMillis(2)-1) shouldEqual "1 minute"
    }

    "print duration in minutes when greater then two minutes and less then one hour" in {
      prettyPrint(MINUTES.toMillis(2)) shouldEqual "2 minutes"
      prettyPrint(HOURS.toMillis(1)-1) shouldEqual "59 minutes"
    }
  }

  "prettyPrint in hours" should {

    "print duration in hour when greater then 60 minutes and less then two hours" in {
      prettyPrint(HOURS.toMillis(1)) shouldEqual "1 hour"
      prettyPrint(HOURS.toMillis(2)-1) shouldEqual "1 hour"
    }

    "print duration in hours when greater then two hours and less then a day" in {
      prettyPrint(HOURS.toMillis(2)) shouldEqual "2 hours"
      prettyPrint(DAYS.toMillis(1)-1) shouldEqual "23 hours"
    }
  }

  "prettyPrint in days" should {

    "print duration in day when greater then 24 hours and less then two days" in {
      prettyPrint(DAYS.toMillis(1)) shouldEqual "1 day"
      prettyPrint(DAYS.toMillis(2)-1) shouldEqual "1 day"
    }

    "print duration in days when greater then then a day" in {
      prettyPrint(DAYS.toMillis(2)) shouldEqual "2 days"
      prettyPrint(DAYS.toMillis(62)) shouldEqual "62 days"
    }
  }
}
