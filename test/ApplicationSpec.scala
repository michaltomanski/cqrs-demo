import controllers.TimeDTO
import org.specs2.mutable._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends Specification {

  "Application" should {

    "accept time" in new WithApplication{
      private val tuples: Seq[(String, String)] = Seq(("content-type", "application/json"))
      val b = Json.toJson(TimeDTO("Michal", 1056))
      val r = route(FakeRequest(POST, "/time", FakeHeaders(tuples), b)).get
      status(r) must equalTo(OK)
    }

  }
}
