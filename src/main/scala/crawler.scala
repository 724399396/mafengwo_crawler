import java.io._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.{Date, Scanner}

import org.jsoup.{HttpStatusException, Jsoup}
import org.jsoup.nodes.Document
import sun.audio.{AudioPlayer, AudioStream}

object crawler{
  def title(doc: Document): String ={
    val title = doc.title()
    val wantedTitle =  title.substring(0, title.length - 6)
    if (wantedTitle == "请输入验证码") {
      val file="D:\\work\\mafengwo\\c.mp3";
      Runtime.getRuntime().exec("cmd /c start "   +   file.replaceAll(" ", "\" \""));
    }
    wantedTitle
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
    //if(doc.select("div.main").size() > 0)
    val time = try {
      doc.select("span.time").first().text()
    } catch { case _: NullPointerException =>
      try {
        doc.select("span.date").first().text()
      } catch {
        case _: NullPointerException => doc.select("span.wap_time").first().text()
      }
    }
    try {
      new Timestamp(timeParse2.parseObject(time)
        .asInstanceOf[Date].getTime)
    } catch {
      case _:Exception =>
        new Timestamp(timeParse1.parseObject(time)
        .asInstanceOf[Date].getTime)
    }
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
      try {
        println(url)
        val doc = Jsoup.connect(url).
          header("Accept", "text/html").timeout(0).userAgent("Mozilla/5.0").get()
        val travel = new Travel(id.toInt, title(doc), content(doc), author(doc), time(doc), url)
        println(travel)
        DBOperation.saveTravel(travel)
        //TimeUnit.SECONDS.sleep(1);
      } catch {
        case _ : HttpStatusException => DBOperation.unExistUrl(url)
      }
    }
//    println(content(Jsoup.parse(new File("2.html"), "utf-8")))
  }
}

