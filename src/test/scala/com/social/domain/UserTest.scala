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

    "result in creating a child actor with the name 'wall'" in {
      system.actorOf(User.props("Bob"), "bob")
      TestProbe().expectActor("/user/bob/wall")
    }

    "result in subscribing to its 'wall'" in {
      val timeline = TestProbe()
      val timelineMaker = (_: ActorRefFactory) => timeline.ref
      val wall = TestProbe()
      val wallMaker = (_: ActorRefFactory) => wall.ref
      val user = system.actorOf(User.props("Chris", timelineMaker, wallMaker), "chris")

      timeline.expectMsg(Timeline.Subscribe(wall.ref))
    }
  }

  "Sending Post to User" should {

    "result in sending Post to users Timeline" in {

      val timeline = TestProbe()
      val timelineMaker = (_: ActorRefFactory) => timeline.ref
      val wall = TestProbe()
      val wallMaker = (_: ActorRefFactory) => wall.ref

      val user = system.actorOf(User.props("John", timelineMaker, wallMaker), "john")

      timeline.expectMsg(Timeline.Subscribe(wall.ref))

      user ! User.Post("I love the weather today")

      timeline.expectMsg(Timeline.Publish("I love the weather today"))

    }

  }

  "Retrieving posts from User" should {

    "result in forwarding GetPosts to users Timeline" in {

      val timeline = system.actorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      val wall = system.actorOf(Props(new Wall()))

      val user = system.actorOf(User.props("Jim", m => timeline, w => wall), "jim")

      user ! User.Post("I love the weather today")

      user ! User.GetPosts

      expectMsg(List(Post("I love the weather today", 10000)))

    }

  }

  "Sending Subscribe to User" should {

    "result in sending SubscribeToWall to the subscribed user" in {

      val timeline = TestProbe()
      val timelineMaker = (_: ActorRefFactory) => timeline.ref
      val wall = TestProbe()
      val wallMaker = (_: ActorRefFactory) => wall.ref

      val user = system.actorOf(User.props("Tim", timelineMaker, wallMaker), "Tim")

      val subscribedUser = TestProbe()

      user ! User.SubscribeTo(subscribedUser.ref)

      subscribedUser.expectMsg(User.SubscribeToWall(wall.ref))
    }

  }

  "Sending SubscribeToWall to User" should {

    "result in sending Subscribe to its Timeline" in {

      val timeline = TestProbe()
      val timelineMaker = (_: ActorRefFactory) => timeline.ref
      val wall = TestProbe()
      val wallMaker = (_: ActorRefFactory) => wall.ref

      val user = system.actorOf(User.props("Tom", timelineMaker, wallMaker), "Tom")

      timeline.expectMsg(Timeline.Subscribe(wall.ref))

      user ! User.SubscribeToWall(wall.ref)

      timeline.expectMsg(Timeline.Subscribe(wall.ref))
    }

  }
}
