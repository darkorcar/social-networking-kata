package com.social.domain

import akka.actor.Props
import com.social.BaseAkkaSpec
import com.social.domain.Wall.GetWall

class WallTest extends BaseAkkaSpec("WallTest") {

  "Sending Publish to a Wall" should {

    "result in adding post to the list of published posts" in {

      val wall = system.actorOf(Props(new Wall()))

      val post = Post("I love the wather today", 10000)

      wall ! Wall.Publish("Alice", post)

      wall ! GetWall

      expectMsg(List(WallPost("Alice", post)))
    }

  }

  "Sending GetWall to a Wall" should {

    "result in sending published posts sorted by timestamp to the sender" in {

      val wall = system.actorOf(Props(new Wall()))

      val post1 = Post("I love the weather today", 10000)
      val post2 = Post("Me too", 20000)
      val post3 = Post("Hi from Peru", 90000)

      wall ! Wall.Publish("Alice", post1)
      wall ! Wall.Publish("Bob", post2)
      wall ! Wall.Publish("Chris", post3)

      wall ! GetWall

      expectMsg(List(
        WallPost("Chris", post3),
        WallPost("Bob", post2),
        WallPost("Alice", post1)
      ))
    }

  }
}
