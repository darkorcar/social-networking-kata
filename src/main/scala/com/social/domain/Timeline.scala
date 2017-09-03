package com.social.domain

import akka.actor.{Actor, Props}
import com.social.domain.Timeline.Publish

class Timeline extends Actor {

  override def receive = {
    case Publish(text) => ""
  }

}

object Timeline {

  case class Publish(text: String)

  def props: Props = Props(new Timeline())

}