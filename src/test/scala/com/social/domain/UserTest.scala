package com.social.domain

import akka.actor.{ActorRef, ActorRefFactory, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.social.{BaseAkkaSpec, FixedClock}

import scala.language.postfixOps

class UserTest extends BaseAkkaSpec("UserActorTest") with FixedClock {

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
        "bob")

      user ! User.Post("I love the weather today")

      timeline.expectMsg(Timeline.Publish("I love the weather today"))

    }

  }

  "Retrieving posts from User" should {

    "result in forwarding GetPosts to users Timeline" in {

      val timeline = system.actorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      val user = system.actorOf(
        User.props("Alice", m => timeline), "john")

      user ! User.Post("I love the weather today")

      user ! User.GetPosts

      expectMsg(List(Post("I love the weather today", 10000)))

    }

  }

  "Sending Follow to User" should {

    "result in adding followed user to the list of following users" in {

//      val timeline = TestProbe()
//      val timelineMaker = (_: ActorRefFactory) => timeline.ref
//      val user = TestActorRef(new User("john", timelineMaker))
//      val followed = TestProbe()
//
//      user ! User.Follow(followed.ref)
//
//      user.underlyingActor.following shouldBe List()

    }

  }

}
