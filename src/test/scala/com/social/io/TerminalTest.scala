package com.social.io

import com.social.BaseSpec

class TerminalTest extends BaseSpec with Terminal {

  "Calling Command.apply" should {

    "create Unknown command for illegal input" in {

      Command("unknown") shouldEqual Command.Unknown("unknown")

    }

    "create Quit command for q | quite | exit input" in {

      Command("q") shouldEqual Command.Quit
      Command("quit") shouldEqual Command.Quit
      Command("exit") shouldEqual Command.Quit

    }

    "create Post command for user post input" in {

      Command("Alice -> I love the weather today") shouldEqual Command.Post("Alice", "I love the weather today")
      Command("Alice  -> I love the weather today") shouldEqual Command.Post("Alice", "I love the weather today")
      Command("Alice ->   I love the weather today") shouldEqual Command.Post("Alice", "I love the weather today")

      Command("Alice-> I love the weather today") shouldEqual Command.Unknown("Alice-> I love the weather today")

    }
  }

}
