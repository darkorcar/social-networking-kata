package com.social.io

import scala.util.parsing.combinator.RegexParsers

trait Terminal {

  protected sealed trait Command

  protected object Command {

    case class Unknown(command: String) extends Command

    def apply(command:String): Command =
      CommandParser.parseAsCommand(command)

  }

  private object CommandParser extends RegexParsers {


    def parseAsCommand(s: String): Command =
      Command.Unknown(s)

  }


}
