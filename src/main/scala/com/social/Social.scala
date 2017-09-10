package com.social

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, Props}
import com.social.domain.User

class Social(userMaker: (ActorRefFactory, String) => ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case Social.UserPost(userId, text) =>
      val userRef = getOrCreateUser(userId)
      userRef ! User.Post(text)
  }

  private def getOrCreateUser(userId: String): ActorRef = {
    context.child(userId) match {
      case Some(child) => child
      case None        => createUser(userId)
    }
  }

  private def createUser(userId: String): ActorRef = {
    log.debug(s"""creating user "$userId"""")
    userMaker(context, userId)
  }
}

object Social {

  case class UserPost(user: String, text: String)

  def props: Props = Props(new Social(userMaker))

  def props(maker: (ActorRefFactory, String) => ActorRef): Props = Props(new Social(maker))

  private val userMaker = (maker: ActorRefFactory, name: String) => maker.actorOf(User.props(name), name)

}