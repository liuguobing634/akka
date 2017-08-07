package lew.bing.joda


import org.joda.time.{Days, LocalDate}

/**
  * Created by 刘国兵 on 2017/7/30.
  */
object JodaMain1 {

  def main(args: Array[String]): Unit = {
    val now = LocalDate.now()
    println(dayToNewYear(now).getDays)
  }

  def dayToNewYear(fromDate: LocalDate): Days = {
    val newYear = fromDate.plusYears(1).withDayOfYear(1)
    Days.daysBetween(fromDate,newYear)
  }

}
