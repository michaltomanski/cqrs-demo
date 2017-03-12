package controllers

import akka.persistence.journal.{Tagged, WriteEventAdapter}

class AllEventsTaggingAdapter extends WriteEventAdapter {

  override def manifest(event: Any): String = event.getClass.getName

  override def toJournal(event: Any): Any = Tagged(event, Set("all"))

}

object TaggingAdapter {

  def tag(event: Any, tags: Set[String]): Any = Tagged(event, tags)

}
