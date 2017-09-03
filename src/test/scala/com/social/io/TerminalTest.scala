package com.social.io

import com.social.BaseSpec

class TerminalTest extends BaseSpec with Terminal {

  "Calling Command.apply" should {

    "create Unknown command for illegal input" in {

      Command("unknown something") shouldEqual Command.Unknown("unknown something")

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

    "create Posts command for user read post input" in {

      Command("Alice") shouldEqual Command.Posts("Alice")

      Command("Alice something") shouldEqual Command.Unknown("Alice something")

    }

    "create Follow command for user follows other user" in {

      Command("Charlie follows Alice") shouldEqual Command.Follow("Charlie", "Alice")

      Command("Charlie loves Alice") shouldEqual Command.Unknown("Charlie loves Alice")

    }

    "create ShowWall command for user wall input" in {

      Command("Charlie wall") shouldEqual Command.ShowWall("Charlie")

      Command("Charlie wally") shouldEqual Command.Unknown("Charlie wally")

    }
  }

}
