package samtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class AlertAdd extends Simulation {
  val baseURL = "http://10.240.136.204:8080"
  val csvFeeder = csv("alert_terms.csv").queue // use a comma separator

  val httpConf = http
    .baseURL(baseURL)
    .acceptCharsetHeader("utf-8;q=0.7,*;qc=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .disableFollowRedirect

  val header = Map(
    "Keep-Alive" -> "115",
    "Content-Type" -> "application/json; charset=UTF-8")

  val scn = scenario("alertAdd").repeat(csvFeeder.records.size) {
    feed(csvFeeder)
      .exec(http("alertAdd")
      .post("/springstock/alertterms")
      .body(StringBody( """{"cid": "${cid}","funcid": "${funcid}","tab": "${tab}","stk": "${stk}","val": "${val}","msgtype": "${msgtype}","expire": "${expire}","xcnts": "${xcnts}","cstamp": "${cstamp}","staff": "${staff}","idx": "${idx}"}""")).asJSON
      .headers(header).check(status.is(200)))
  }

  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
