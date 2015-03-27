import scala.beans.BeanProperty

/**
 * Created by li-wei on 2015/3/25.
 */
class Travel(@BeanProperty val cityId: Int, @BeanProperty val title: String,
             @BeanProperty val contentText: String, @BeanProperty val author: String,
             @BeanProperty val time: java.sql.Timestamp, @BeanProperty val url: String) {
  override def toString =
    "cityId: " + cityId + " title: " + title + " time: " + time
}
