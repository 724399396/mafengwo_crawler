import akka.actor.{Actor, Props, ActorSystem}
import akka.routing.RoundRobinRouter
import org.jsoup.{HttpStatusException, Jsoup}

/**
 * Created by li-wei on 2015/3/27.
 */
import crawler._
object actorVersion extends App {
  sealed trait Url
  case class MfwUrl(id: Int,url: String) extends Url

  class Crawler extends Actor {
    def receive = {
      case MfwUrl(id, url) => {
        try {
          println(url)
          val doc = Jsoup.connect(url).
            header("Accept", "text/html").timeout(0).userAgent("Mozilla/5.0").get()
          val travel = new Travel(id.toInt, title(doc), content(doc), author(doc), time(doc), url)
          println(travel)
          DBOperation.saveTravel(travel)
        }catch {
          case _ : HttpStatusException => DBOperation.unExistUrl(url)
        }
      }

    }
  }

  val system = ActorSystem("crawlerSystem")
  val crawler = system.actorOf(Props[Crawler].withRouter(RoundRobinRouter(8)), name = "crawler")

  for(id <- DBOperation.getAllId(); url <- DBOperation.getUrls(id)) {
    crawler ! new MfwUrl(id,url)
  }
}
