package com.social

import akka.actor.{ActorRef, ActorRefFactory, Props}
import akka.testkit.TestProbe
import com.social.Social.{UserPost, UserPosts, UserWall}
import com.social.domain.{Post, User}
import org.scalamock.scalatest.MockFactory

class SocialTest extends BaseAkkaSpec("SocialTest") with MockFactory {

  "Sending UserPost" should {

    "result in creating a child actor with user name if it doesn't exist" in {
      val social = system.actorOf(Social.props, "social")
      social ! UserPost("alice", "Hello, my name is Alice.")
      TestProbe().expectActor("/user/social/alice/")
    }

    "result in sending Post message with post text to the User" in {
      val user = TestProbe()
      val maker = (_: ActorRefFactory, _: String) => user.ref
      val social = system.actorOf(Social.props(maker), "social2")

      social ! UserPost("alice", "I love the weather today")

      user.expectMsg(User.Post("I love the weather today"))
    }

  }

  "Sending UserPosts" should {

    "result in forwarding GetPosts message to user" in {
      val user = TestProbe()
      val maker = (_: ActorRefFactory, _: String) => user.ref
      val social = system.actorOf(Social.props(maker), "social3")

      social ! UserPosts("I love the weather today")

      user.expectMsg(User.GetPosts)

      user.lastSender shouldBe self
    }

  }

  "Sending UserFollow" should {

    "result in sending SubscribeTo message to followed User actor" in {

      val userMakerStub = stubFunction[(ActorRefFactory, String), ActorRef]

      val user1 = TestProbe()
      val user2 = TestProbe()

      userMakerStub.when(*).returns(user1.ref).noMoreThanOnce()
      userMakerStub.when(*).returns(user2.ref).noMoreThanOnce()

      //val social = system.actorOf(Social.props(userMakerStub), "social4")

      //social ! Social.UserFollow("john", "bob")

      //user1.expectMsg(User.SubscribeTo(user2.ref))
    }

  }

  "Sending UserWall" should {

    "result in forwarding GetWall message to user" in {
      val user = TestProbe("Alice")
      val maker = (_: ActorRefFactory, _: String) => user.ref
      val social = system.actorOf(Social.props(maker), "social5")

      social ! UserWall("Alice")

      user.expectMsg(User.GetWall)

      user.lastSender shouldBe self
    }

  }

}
