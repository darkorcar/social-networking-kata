package com.social.io

import scala.util.parsing.combinator.RegexParsers

trait Terminal {

  protected sealed trait Command

  protected object Command {

    case class Unknown(command: String) extends Command

    case object Quit extends Command

    case class Post(user: String, post: String) extends Command

    case class Posts(user: String) extends Command

    case class Follow(user: String, followed: String) extends Command

    case class ShowWall(user: String) extends Command

    def apply(command:String): Command =
      CommandParser.parseAsCommand(command)

  }

  private object CommandParser extends RegexParsers {

    def parseAsCommand(s: String): Command =
      parseAll(parser, s) match {
        case Success(command, _) => command
        case _ => Command.Unknown(s)
      }

    def quit: Parser[Command.Quit.type] =
      "quit|q|exit".r ^^ (_ => Command.Quit)

    def createPost: Parser[Command.Post] =
      user ~ ("->".r ~> text) ^^ {
        case user ~ post =>
          Command.Post(user, post)
      }

    def listPosts: Parser[Command.Posts] =
      "(?:^|(?:[.!?]\\s))(\\w+)(?:$)".r ^^ {
        case user => Command.Posts(user)
      }

    def follow: Parser[Command.Follow] =
      user ~ ("follows".r ~> "(\\w+)".r) ^^ {
        case user ~ followed => Command.Follow(user, followed)
      }

    def showWall: Parser[Command.ShowWall] =
      user <~ "wall".r ^^ {
        case user => Command.ShowWall(user)
      }

    def user: Parser[String] =
    "(?:^|(?:[.!?]\\s))(\\w+)(?=\\s)".r ^^ (_.toString)

    def text: Parser[String] =
      ".*".r ^^ (_.toString)

    private val parser: CommandParser.Parser[Command] =
      quit | createPost | listPosts | follow | showWall
  }


}
