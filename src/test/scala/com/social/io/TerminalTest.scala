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
    
  }

}
