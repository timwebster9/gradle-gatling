package default

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")

	val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Origin" -> "http://localhost:8080",
		"Upgrade-Insecure-Requests" -> "1")



	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/?form")
			.headers(headers_0))
		.pause(13)
		.exec(http("request_1")
			.post("/?form")
			.headers(headers_1)
			.formParam("id", "")
			.formParam("summary", "test")
			.formParam("text", "test message"))
		.pause(3)
		.exec(http("request_2")
			.get("/delete/1")
			.headers(headers_0))
		.pause(1)
		.exec(http("request_3")
			.get("/?form")
			.headers(headers_0))
		.pause(11)
		.exec(http("request_4")
			.post("/?form")
			.headers(headers_1)
			.formParam("id", "")
			.formParam("summary", "test message 2")
			.formParam("text", "second test message"))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}