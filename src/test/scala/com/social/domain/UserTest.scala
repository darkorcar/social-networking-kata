package com.social.domain

import akka.actor.ActorRefFactory
import akka.testkit.TestProbe
import com.social.BaseAkkaSpec

import scala.language.postfixOps

class UserTest extends BaseAkkaSpec("UserActorTest") {

  "Creating User" should {

    "result in creating a child actor with the name 'timeline'" in {
      system.actorOf(User.props("Alice"), "alice")
      TestProbe().expectActor("/user/alice/timeline")
    }

  }

  "Sending Post to User" should {

    "result in sending Post to users Timeline" in {

      val timeline = TestProbe()
      val user = system.actorOf(
        User.props("Alice", (_: ActorRefFactory) => timeline.ref),
        "alice")

      user ! User.Post("I love the weather today")

      timeline.expectMsg(Timeline.Publish("I love the weather today"))

    }

  }

}
