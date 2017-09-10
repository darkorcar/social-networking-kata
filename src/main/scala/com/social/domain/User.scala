package com.social.domain

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, Props}

import scala.collection.mutable

class User(name: String, timelineMaker: ActorRefFactory => ActorRef)
    extends Actor
    with ActorLogging {

  private val timeline = timelineMaker(context)

  private var following: mutable.MutableList[ActorRef] =
    mutable.MutableList.empty

  override def receive = {
    case User.Post(text) =>
      log.debug("""posting "{}"""", text)
      timeline ! Timeline.Publish(text)
    case User.GetPosts =>
      log.debug("retrieving posts")
      timeline forward Timeline.GetPosts
    case User.Follow(user) =>
      log.debug("following {}", user.path.name)
      following += user
  }

}

object User {

  case class Post(text: String)

  case object GetPosts

  case class Follow(user: ActorRef)

  def props(name: String): Props = Props(new User(name, timelineMaker))

  def props(name: String, maker: ActorRefFactory => ActorRef): Props =
    Props(new User(name, maker))

  private val timelineMaker = (maker: ActorRefFactory) => maker.actorOf(Props[Timeline], "timeline")

}
