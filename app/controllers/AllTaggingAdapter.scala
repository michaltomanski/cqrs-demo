package controllers

import akka.persistence.journal.{Tagged, WriteEventAdapter}

class AllEventsTaggingAdapter extends WriteEventAdapter {

  override def manifest(event: Any): String = event.getClass.getName

  override def toJournal(event: Any): Any = {
    println("Tagging")
    Tagged(event, Set("all"))
  }
}

object TaggingAdapter {

  def tag(event: Any, tags: Set[String]): Any = {
    println("Tagging")
    Tagged(event, tags)
  }
}
