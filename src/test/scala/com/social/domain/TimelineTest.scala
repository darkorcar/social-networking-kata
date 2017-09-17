package com.social.domain

import java.time.Clock

import akka.actor.Props
import akka.testkit.TestProbe
import com.social.{BaseAkkaSpec, FixedClock}
import org.scalamock.scalatest.MockFactory

class TimelineTest extends BaseAkkaSpec("TimelineTest") with FixedClock with MockFactory {

  "Sending Publish to Timeline" should {

    "result in adding published post to the list of posts" in {

      val timeline = system.actorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      timeline ! Timeline.Publish("I love the weather today")

      timeline ! Timeline.GetPosts

      expectMsg(List(Post("I love the weather today", 10000)))
    }

    "result in sending Wall.Publish message to the subscribed walls" in {

      val parent = TestProbe("Alice")

      val timeline = parent.childActorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      val subscribedWall = TestProbe()

      timeline ! Timeline.Subscribe(subscribedWall.ref)

      timeline ! Timeline.Publish("I love the weather today")

      subscribedWall.expectMsg(Wall.Publish("Alice-2", Post("I love the weather today", 10000)))
    }
  }

  "Sending GetPosts to Timeline" should {

    "result in sending List of Posts to sender ordered desc by timestamp" in {

      val clockStub = stub[Clock]

      (clockStub.millis _).when().returns(1).noMoreThanOnce()
      (clockStub.millis _).when().returns(2).noMoreThanOnce()
      (clockStub.millis _).when().returns(3)

      val timeline = system.actorOf(Props(new Timeline {
        override protected val clock = clockStub
      }))

      timeline ! Timeline.Publish("I love the weather today")
      timeline ! Timeline.Publish("Hello")
      timeline ! Timeline.Publish("Hi")

      timeline ! Timeline.GetPosts

      expectMsg(
        List(
          Post("Hi", 3),
          Post("Hello", 2),
          Post("I love the weather today", 1)
        ))
    }

  }

  "Sending Subscribe to Timeline" should {

    "result in adding wall reference to subscribers if it's not subscribed all ready" in {

      val parent = TestProbe("Alice")

      val timeline = parent.childActorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      val wall = TestProbe()

      timeline ! Timeline.Subscribe(wall.ref)

      timeline ! Timeline.Publish("I love the weather today")

      wall.expectMsg(Wall.Publish("Alice-4", Post("I love the weather today", 10000)))

      timeline ! Timeline.Subscribe(wall.ref)

      wall.expectNoMsg()
    }

    "result in publishing previous posts to newly subscribed Wall" in {

      val parent = TestProbe("Alice")

      val timeline = parent.childActorOf(Props(new Timeline {
        override protected val clock = fixedClock(10000)
      }))

      val wall = TestProbe()

      timeline ! Timeline.Publish("I love the weather today")

      timeline ! Timeline.Subscribe(wall.ref)

      wall.expectMsg(Wall.Publish("Alice-6", Post("I love the weather today", 10000)))
    }
  }

}
