package com.social.domain

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.social.domain.Timeline.{GetPosts, Publish, Subscribe}
import com.social.util.ClockProvider

import scala.collection.mutable

class Timeline extends Actor with ActorLogging with ClockProvider {

  private val username = context.parent.path.name

  private val posts: mutable.MutableList[Post] =
    mutable.MutableList.empty

  private val subscribersWalls: mutable.LinkedHashSet[ActorRef] =
    mutable.LinkedHashSet.empty

  override def receive = {

    case Publish(text) =>
      val post = Post(text, now)
      posts += post
      publishToSubscribersWall(post)

    case GetPosts =>
      sender() ! posts
        .sortBy(post => post.timestamp)
        .reverse
        .toList

    case Subscribe(wall) =>
      log.debug(s"subscribe $wall")
      subscribeIfNotAlready(wall)
  }

  private def subscribeIfNotAlready(wall: ActorRef): Unit = {
    subscribersWalls.find(_ == wall) match {
      case None =>
        publishPreviousPosts(wall)
        subscribersWalls += wall
      case _ =>
    }
  }

  private def publishPreviousPosts(wall: ActorRef): Unit = {
    posts.foreach(wall ! Wall.Publish(username, _))
  }

  private def publishToSubscribersWall(post: Post): Unit = {
    subscribersWalls.foreach { wall =>
      wall ! Wall.Publish(username, post)
    }
  }

}

object Timeline {

  case class Publish(text: String)

  case class Subscribe(wall: ActorRef)

  case object GetPosts

  def props: Props = Props(new Timeline())

}

case class Post(text: String, timestamp: Long)
