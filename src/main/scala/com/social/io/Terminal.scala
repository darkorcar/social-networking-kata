package com.social.io

import scala.util.parsing.combinator.RegexParsers

trait Terminal {

  protected sealed trait Command

  protected object Command {

    case class Unknown(command: String) extends Command

    case object Quit extends Command

    def apply(command:String): Command =
      CommandParser.parseAsCommand(command)

  }

  private object CommandParser extends RegexParsers {

    def parseAsCommand(s: String): Command =
      parseAll(parser, s) match {
        case Success(command, _) => command
        case _ => Command.Unknown(s)
      }

    private def quit: Parser[Command.Quit.type] =
      "quit|q|exit".r ^^ (_ => Command.Quit)

    private val parser: CommandParser.Parser[Command] =
      quit
  }


}
