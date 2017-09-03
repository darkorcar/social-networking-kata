package com.social.domain

import akka.actor.Props
import com.social.{BaseAkkaSpec, FixedClock}

class TimelineTest extends BaseAkkaSpec("TimelineTest") with FixedClock {

  "Sending Publish to Timeline" should {

    "result in adding published post to the list" in {

      val timeline = system.actorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      timeline ! Timeline.Publish("I love the weather today")

      timeline ! Timeline.GetPosts

      expectMsg(List(Post("I love the weather today", 10000)))

    }

  }

}
