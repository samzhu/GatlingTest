package samtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

//一定要繼承Simulation，格林機關槍才可以識別你的腳本
class AlertAdd extends Simulation {
  val baseURL = "http://10.240.136.204:8080"
  //測試資料來源為csv檔案,queue如果用完會出現Exception，如果你希望無限循環應該使用csv("alert_terms.csv").circular
  val csvFeeder = csv("alert_terms.csv").queue
  //基本的http設定
  val httpConf = http
    .baseURL(baseURL)
    .acceptCharsetHeader("utf-8;q=0.7,*;qc=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .disableFollowRedirect

  //綁定Content-Type
  val header = Map(
    "Content-Type" -> "application/json; charset=UTF-8")

  //主要執行指令
  //repeat表示我要執行的次數，這邊指定跟檔案一樣多筆才不會爆掉
  val scn = scenario("alertAdd").repeat(csvFeeder.records.size) {
    feed(csvFeeder)
      .exec(http("alertAdd")
      .post("/springstock/alertterms")
      //Http Body為Json格式
      .body(StringBody( """{"cid": "${cid}","funcid": "${funcid}","tab": "${tab}","stk": "${stk}","val": "${val}","msgtype": "${msgtype}","expire": "${expire}","xcnts": "${xcnts}","cstamp": "${cstamp}","staff": "${staff}","idx": "${idx}"}""")).asJSON
      .headers(header)
      //check是成功條件
      .check(status.is(200)))
  }
  //注入模擬單一使用者
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
