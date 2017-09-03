package com.social.domain

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

}
