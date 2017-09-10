package com.social.domain

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, Props}

class User(name: String, timelineMaker: ActorRefFactory => ActorRef)
    extends Actor
    with ActorLogging {

  private val timeline = timelineMaker(context)

  override def receive = {
    case User.Post(text) =>
      log.debug("""posting "{}"""", text)
      timeline ! Timeline.Publish(text)
    case User.GetPosts =>
      log.debug("retrieving posts")
      timeline forward Timeline.GetPosts
  }

}

object User {

  case class Post(text: String)

  case object GetPosts

  def props(name: String): Props = Props(new User(name, timelineMaker))

  def props(name: String, maker: ActorRefFactory => ActorRef): Props =
    Props(new User(name, maker))

  private val timelineMaker = (maker: ActorRefFactory) => maker.actorOf(Props[Timeline], "timeline")

}
