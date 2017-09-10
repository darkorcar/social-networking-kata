package com.social

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import com.social.domain.Post
import com.social.io.Terminal
import com.social.util.ClockProvider

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.{DurationInt, _}
import scala.io.StdIn

class SocialApp(system: ActorSystem) extends Terminal with ClockProvider {

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
      case Command.Posts(user) =>
        printPosts(user)
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

  private def printPosts(user: String): Unit = {
    implicit val timeout: Timeout = Duration(5, SECONDS)
    val response = (social ? Social.UserPosts(user)).mapTo[List[Post]]
    val r = Await.result(response, 5 second)
    r.foreach { post =>
      val elapsedTime = now - post.timestamp
      println(s"${post.text} (${prettyPrint(elapsedTime)} ago)")
    }
  }

  private def prettyPrint(millis: Long) = {
    val secs = millis / 1000
    val mins = (millis / 1000) / 60
    val hours = (millis / 1000) / (60 * 60)
    val days = ((millis / 1000) / (60 * 60)) / 24

    if (days > 0) s"${Duration.create(days, TimeUnit.DAYS).toString()}"
    else if (hours > 0) s"${Duration.create(hours, TimeUnit.HOURS).toString()}"
    else if (mins > 0) s"${Duration.create(mins, TimeUnit.MINUTES).toString()}"
    else if (secs > 0) s"${Duration.create(secs, TimeUnit.SECONDS).toString()}"
    else s"1 second ago"
  }
}

object SocialApp {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("social-system")

    val socialApp = new SocialApp(system)

    socialApp.run()

  }

}
