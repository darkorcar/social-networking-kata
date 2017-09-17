package com.social

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import com.social.domain.{Post, WallPost}
import com.social.io.Terminal
import com.social.util.{ClockProvider, PrettyPrintDuration}

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.{DurationInt, _}
import scala.io.StdIn

class SocialApp(system: ActorSystem) extends Terminal with ClockProvider with PrettyPrintDuration {

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
      case Command.Follow(user, followed) =>
        follow(user, followed)
        commandLoop()
      case Command.ShowWall(user) =>
        showWall(user)
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

  private def follow(user: String, followed: String): Unit = {
    social ! Social.UserFollow(user, followed)
  }

  private def showWall(user: String): Unit = {
    implicit val timeout: Timeout = Duration(5, SECONDS)
    val respose = (social ? Social.UserWall(user)).mapTo[List[WallPost]]
    val r = Await.result(respose, 5 seconds)
    r.foreach { userPost =>
      val elapsedTime = now - userPost.post.timestamp
      println(s"${userPost.user} - ${userPost.post.text} (${prettyPrint(elapsedTime)} ago)")
    }
  }

}

object SocialApp {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("social-system")

    val socialApp = new SocialApp(system)

    socialApp.run()

  }

}
