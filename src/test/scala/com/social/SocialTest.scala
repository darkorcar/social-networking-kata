package com.social

import akka.actor.{ActorRef, ActorRefFactory, Props}
import akka.testkit.TestProbe
import com.social.Social.{UserPost, UserPosts}
import com.social.domain.{Post, User}

class SocialTest extends BaseAkkaSpec("SocialTest") {

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

    "result in sending Follow message to User actor" in {

      val social = system.actorOf(Social.props, "social4")

      social ! Social.UserPost("john", "hello")

      val userJohn = TestProbe().expectActor("/user/social4/john/")
      val testProbeJohn = TestProbe()
      testProbeJohn.watch(userJohn)

      social ! Social.UserPost("bob", "hi")

      val userBob = TestProbe().expectActor("/user/social4/bob/")

      social ! Social.UserFollow("john", "bob")

      //testProbeJohn.expectMsg(User.Follow(userBob))
    }

  }

}
