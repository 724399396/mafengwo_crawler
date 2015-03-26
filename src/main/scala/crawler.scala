import java.io._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.{Date, Scanner}

import org.jsoup.{HttpStatusException, Jsoup}
import org.jsoup.nodes.Document

object crawler{
  def title(doc: Document): String ={
    val title = doc.title()
    title.substring(0, title.length - 6)
  }
  def author(doc: Document): String = {
    if(doc.select("div.main").size() > 0)
      doc.select("a.per_name").first().text()
    else try {
      doc.select("a.name").first().text()
    } catch {case _: Exception => ""}
  }
  val timeParse1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val timeParse2 = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  def time(doc: Document): java.sql.Timestamp = {
    if(doc.select("div.main").size() > 0)
      new Timestamp(timeParse2.parseObject(doc.select("span.time").first().text())
        .asInstanceOf[Date].getTime)
    else //try {
      new Timestamp(timeParse1.parseObject(doc.select("span.date").first().text())
        .asInstanceOf[Date].getTime)
    //} catch {case _: Exception => null }
  }
  def content(doc: Document): String = {
    if(doc.select("div.main").size() > 0)
      doc.select("div.va_con").first.select("p").text()
    else try {
      doc.select("div.a_con_text.cont").first.select("p").text()
    } catch { case _: Exception => "" }
  }
  def main(args: Array[String]) = {
//    lazy val doc = Jsoup.connect("http://www.mafengwo.cn/i/3086360.html").
//      header("Accept", "text/plain").timeout(0).get()
//    lazy val out = new OutputStreamWriter(new FileOutputStream("2.html"), "UTF-8")
//    out.write(doc.toString)
    for(id <- DBOperation.getAllId(); url <- DBOperation.getUrls(id)) {
      println(url)
      try {
        val doc = Jsoup.connect(url).
          header("Accept", "text/html").timeout(0).userAgent("Mozilla").get()
        val travel = new Travel(id.toInt, title(doc), content(doc), author(doc), time(doc), url)
        println(travel)
        DBOperation.saveTravel(travel)
        TimeUnit.SECONDS.sleep(1);
      } catch {
        case _ : HttpStatusException =>
      }
    }
//    println(content(Jsoup.parse(new File("2.html"), "utf-8")))
  }
}

