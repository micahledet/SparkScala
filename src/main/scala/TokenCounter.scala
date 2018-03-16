import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._

/* Create tokens from words in a text file using regular expressions,
count occurences, sort results, print */

object TokenCounter {

  def main(args: Array[String]) {

    // Set logger to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a local SparkContext
    val sc = new SparkContext("local[*]", "TokenCounter")

    // Load each line of text into an RDD
    val textRDD = sc.textFile("../1001_nights.txt")

    // Split using a regular expression that extracts words, then make lowercase
    val tokens = textRDD.
      flatMap(x => x.split("\\W+")).
      map(x => x.toLowerCase())

    // Count word occurences, sort by count
    val tokenCounts = tokens.
      map(x => (x, 1)).
      reduceByKey((x,y) => x+y).
      map(x => (x._2, x._1)).
      sortByKey()

    // Print unique tokens along with their counts
    for (result <- tokenCounts) {
      val count = result._1
      val token = result._2
      println(s"($token, $count)")}

    // Stop spark SparkContext
    sc.stop()
  }
}
