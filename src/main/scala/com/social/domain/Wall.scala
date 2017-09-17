package com.social.domain

import akka.actor.{Actor, Props}

import scala.collection.mutable

class Wall extends Actor {

  private val posts: mutable.MutableList[WallPost] =
    mutable.MutableList.empty

  override def receive = {

    case Wall.Publish(username, post) =>
      posts += WallPost(username, post)

    case Wall.GetWall =>
      sender() ! wallPosts()

  }

  private def wallPosts(): List[WallPost] =
    posts
      .sortBy(wallPost => wallPost.post.timestamp)
      .toList
      .reverse
}

object Wall {

  case object GetWall

  case class Publish(username: String, post: Post)

  def props: Props = Props(new Wall())

}

case class WallPost(user: String, post: Post)
