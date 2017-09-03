package com.social.domain

import akka.actor.{Actor, Props}
import com.social.domain.Timeline.{GetPosts, Publish}
import com.social.util.ClockProvider

import scala.collection.mutable

class Timeline extends Actor with ClockProvider {

  private val posts: mutable.MutableList[Post] =
    mutable.MutableList.empty

  override def receive = {
    case Publish(text) =>
      posts += Post(text, now)
    case GetPosts =>
      sender() ! posts.reverse.toList
  }

}

object Timeline {

  case class Publish(text: String)

  case object GetPosts

  def props: Props = Props(new Timeline())

}

case class Post(text: String, timestamp: Long)
