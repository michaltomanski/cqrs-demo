package controllers

import akka.persistence.journal.{EventAdapter, Tagged, WriteEventAdapter}

class TimeAddedAdapter extends WriteEventAdapter {

  private val tag: (Any) => Any = TaggingAdapter.tag(_, Set("all"))

  override def manifest(event: Any): String = event.getClass.getName

  override def toJournal(event: Any): Any = {
    Tagged(event, Set("all"))
  }
}
