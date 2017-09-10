package com.social

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import com.social.io.Terminal

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

class SocialApp(system: ActorSystem) extends Terminal {

  private val log = Logging(system, getClass.getName)

  private val social = createSocial()

  def run(): Unit = {
    log.warning(
      f"{} running%nEnter "
        + Console.BLUE + "commands" + Console.RESET
        + " into the terminal: "
        + Console.BLUE + "[e.g. `q` or `quit`]" + Console.RESET,
      getClass.getSimpleName
    )
    commandLoop()
    Await.ready(system.whenTerminated, Duration.Inf)
  }

  @tailrec
  private def commandLoop(): Unit = {
    Command(StdIn.readLine()) match {
      case Command.Post(user, message) =>
        post(user, message)
        commandLoop()
      case Command.Quit =>
        system.terminate()
      case Command.Unknown(command) =>
        log.warning("Unknown command {}!", command)
        commandLoop()
    }
  }

  private def createSocial(): ActorRef = {
    system.actorOf(Social.props, "social")
  }

  private def post(user: String, text: String): Unit = {
    social ! Social.UserPost(user, text)
  }
}

object SocialApp {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("social-system")

    val socialApp = new SocialApp(system)

    socialApp.run()

  }

}
