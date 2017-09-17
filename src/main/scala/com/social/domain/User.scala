package com.social.domain

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, Props}
import com.social.domain.User.SubscribeToWall

class User(name: String,
           timelineMaker: ActorRefFactory => ActorRef,
           wallMaker: ActorRefFactory => ActorRef)
    extends Actor
    with ActorLogging {

  private val timeline = timelineMaker(context)

  private val wall = wallMaker(context)

  timeline ! Timeline.Subscribe(wall)

  override def receive = {

    case User.Post(text) =>
      log.debug("""posting "{}"""", text)
      timeline ! Timeline.Publish(text)

    case User.GetPosts =>
      log.debug("retrieving posts")
      timeline forward Timeline.GetPosts

    case User.SubscribeTo(user) =>
      log.debug("subscribe to {}", user.path.name)
      user ! SubscribeToWall(wall)

    case User.SubscribeToWall(subscriberWall) =>
      timeline ! Timeline.Subscribe(subscriberWall)

    case User.GetWall =>
      wall forward Wall.GetWall
  }

}

object User {

  case class Post(text: String)

  case object GetPosts

  case class SubscribeTo(user: ActorRef)

  case class SubscribeToWall(wall: ActorRef)

  case object GetWall

  def props(name: String): Props = Props(new User(name, timelineMaker, wallMaker))

  def props(name: String,
            timelineMaker: ActorRefFactory => ActorRef,
            wallMaker: ActorRefFactory => ActorRef): Props =
    Props(new User(name, timelineMaker, wallMaker))

  private val timelineMaker = (maker: ActorRefFactory) => maker.actorOf(Props[Timeline], "timeline")
  private val wallMaker = (maker: ActorRefFactory) => maker.actorOf(Props[Wall], "wall")

}
