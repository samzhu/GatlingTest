package samtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class StockPost extends Simulation{
  val baseURL = "http://10.240.136.204:8080"
  val duringTime:Int = 60*60*1
  val csvAlert = csv("alert_terms.csv").circular
  val csvSemerging = csv("stock/emerging.txt").circular
  val csvSfut = csv("stock/fut.txt").circular
  val csvSopt = csv("stock/opt.txt").circular
  val csvSotc = csv("stock/otc.txt").circular
  val csvStse = csv("stock/tse.txt").circular

  val httpConf = http
    .baseURL(baseURL)
    .acceptCharsetHeader("utf-8;q=0.7,*;qc=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .disableFollowRedirect

  val header = Map(
    "Content-Type" -> "application/json; charset=UTF-8")

  val alert = scenario("Alert").during(duringTime) {
    feed(csvAlert)
      .exec(http("AlertAdd")
      .post("/springstock/alertterms")
      .body(StringBody( """{"cid": "${cid}","funcid": "${funcid}","tab": "${tab}","stk": "${stk}","val": "${val}","msgtype": "${msgtype}","expire": "${expire}","xcnts": "${xcnts}","cstamp": "${cstamp}","staff": "${staff}","idx": "${idx}"}""")).asJSON
      .headers(header).check(status.is(200)))
      .exec(http("AlertDel")
      .delete("/springstock/alertterms?cid=${cid}&funcid=${funcid}&tab=${tab}&stk=${stk}&val=${val}&msgtype=${msgtype}")
      .headers(header).check(status.is(200)))
  }
  val scn_emerging = scenario("stock_emerging").during(duringTime) {
    feed(csvSemerging)
      .exec(http("stock_emerging")
      .post("/springstock/stock")
      .body(StringBody("""{"tab": "emerging","sid": "${sid}","strikeprice":"${d}" }""")).asJSON
      .headers(header).check(status.is(200)))
  }
  val scn_fut = scenario("stock_fut").during(duringTime) {
    feed(csvSfut)
      .exec(http("stock_fut")
      .post("/springstock/stock")
      .body(StringBody("""{"tab": "fut","sid": "${sid}","strikeprice":"${d}" }""")).asJSON
      .headers(header).check(status.is(200)))
  }
  val scn_opt = scenario("stock_opt").during(duringTime) {
    feed(csvSopt)
      .exec(http("stock_opt")
      .post("/springstock/stock")
      .body(StringBody("""{"tab": "opt","sid": "${sid}","strikeprice":"${d}" }""")).asJSON
      .headers(header).check(status.is(200)))
  }
  val scn_otc = scenario("stock_otc").during(duringTime) {
    feed(csvSotc)
      .exec(http("stock_otc")
      .post("/springstock/stock")
      .body(StringBody("""{"tab": "otc","sid": "${sid}","strikeprice":"${d}" }""")).asJSON
      .headers(header).check(status.is(200)))
  }
  val scn_tse = scenario("stock_tse").during(duringTime) {
    feed(csvStse)
      .exec(http("stock_tse")
      .post("/springstock/stock")
      .body(StringBody("""{"tab": "tse","sid": "${sid}","strikeprice":"${d}" }""")).asJSON
      .headers(header).check(status.is(200)))
  }

  setUp(alert.inject(atOnceUsers(1))
    ,scn_emerging.inject(atOnceUsers(3))
    ,scn_fut.inject(atOnceUsers(3))
    ,scn_opt.inject(atOnceUsers(3))
    ,scn_otc.inject(atOnceUsers(3))
    ,scn_tse.inject(atOnceUsers(3))
  ).protocols(httpConf)


}
