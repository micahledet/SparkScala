import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec

/*Create a broadcast variable mapping id's to names, read data file into RDD,
count the number of ratings per movie in the dataset, sort by count, print results*/

object MostPopularMovies {

  /* Return a map of movie IDs and corresponding movie names. */
  def mapNames() : Map[Int, String] = {

    // Handle character encoding issues:
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // Create a Map of Ints to Strings and populate it
    var movieNames:Map[Int, String] = Map()
    // Use Source to pull each line from the file
     val lines = Source.fromFile("../ml-100k/u.item").getLines()
    // Split on |, check to make sure the line is not empty, add (id, title) to map
     for (line <- lines) {
       var fields = line.split('|')
       if (fields.length > 1) {
        movieNames += (fields(0).toInt -> fields(1))
       }
     }
     return movieNames
  }

  def main(args: Array[String]) {

    // Set logger to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "PopularMovies")

    // Read in each rating line
    val lines = sc.textFile("../ml-100k/u.data")

    // Create a broadcast variable of our ID -> movie name map
    var nameDict = sc.broadcast(mapNames)

    // Extract movie id, count occurrences by id, sort by most occurrences,
    // replace movie id with name from broadcasted map
    val movies = lines.map(x => (x.split("\t")(1).toInt, 1)).
      reduceByKey( (x, y) => x + y ).
      map( x => (x._2, x._1) ).
      sortByKey().
      map( x  => (nameDict.value(x._2), x._1) )


    // Collect results
    val results = movies.collect()

    // Print results
    results.foreach(println)

    // Stop spark context
    sc.stop()
  }
}
