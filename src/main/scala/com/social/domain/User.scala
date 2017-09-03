package com.social.domain

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}

class User(name: String, timelineMaker: ActorRefFactory => ActorRef)
    extends Actor {

  private val timeline = timelineMaker(context)

  override def receive = {
    case _ => "NotImplemented"
  }

}

object User {

  def props(name: String): Props = Props(new User(name, timelineMaker))

  def props(name: String, maker: ActorRefFactory => ActorRef): Props =
    Props(new User(name, maker))

  private val timelineMaker = (maker: ActorRefFactory) =>
    maker.actorOf(Props[Timeline], "timeline")

}
