import java.util

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.{SqlSession, SqlSessionFactoryBuilder}

import scala.collection.mutable


/**
 * All Operation about mafengwo Database
 */
object DBOperation {
  val resource = "dbconf.xml"
  val reader = Resources.getResourceAsReader(resource)
  val sessionFactory = new SqlSessionFactoryBuilder().build(reader)
  val session: SqlSession = sessionFactory.openSession()

  def getUrls(id: Int): List[String] = {
    val statement = "com.qunar.liwei.graduation.mafengwo.getUrls"
    import collection.JavaConversions.asScalaBuffer
    val list: mutable.Buffer[String] = session.selectList(statement,id).asInstanceOf[util.ArrayList[String]]
    session.commit()
    var result = List[String]()
    for(url <- list) result = result :+ url
    result
  }

  def getAllId(): mutable.Buffer[Int] = {
    val statement = "com.qunar.liwei.graduation.mafengwo.getAllId"
    import collection.JavaConversions.asScalaBuffer
    val list: mutable.Buffer[Int] = session.selectList(statement).asInstanceOf[util.ArrayList[Int]]
    session.commit()
    list
  }
  def saveTravel(travel: Travel):Int = {
    if(isTravelExist(travel)) { println(travel.url + " is exist"); 0 }
    else {
      val statement = "com.qunar.liwei.graduation.mafengwo.saveTravel"
      val insert = session.insert(statement, travel)
      session.commit()
      val statement2 = "com.qunar.liwei.graduation.mafengwo.changeUrlState"
      session.update(statement2, travel.url)
      session.commit()
      insert
    }
  }
  def unExistUrl(url: String): Unit = {
    val statement = "com.qunar.liwei.graduation.mafengwo.changeUrlState"
    session.update(statement, url)
    session.commit()
  }
  def isTravelExist(travel: Travel):Boolean = {
    val statement = "com.qunar.liwei.graduation.mafengwo.isTravelExist"
    val id = session.selectOne(statement, travel).asInstanceOf[Int]
    session.commit()
    if (id > 0) true else false
  }

  def main(args: Array[String]):Unit = {
    saveTravel(new Travel(10065,"我又来啦，箭扣......,箭扣长城自助游攻略","","随心所欲(首都)",null,"http://www.mafengwo.cn/i/3259740.html"))
  }
}
