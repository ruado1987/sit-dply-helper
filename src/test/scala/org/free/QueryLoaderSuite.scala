package org.free

import java.io.{ File }

import org.scalatest._
import matchers._
import scalax.io._
import scalax.file._

class QueryLoaderSuite extends fixture.FunSuite with ShouldMatchers {

  type FixtureParam = Output
  
  implicit val fakeDatabaseProvider = new DatabaseProvider {
    
    def database = null
  } 

  override def withFixture( test : OneArgTest ) {
    val filename = "queries.txt"
    val out : Output = Resource.fromFile( filename )

    Path( filename ).deleteIfExists()

    test( out )
  }

  test( "load file" ) { output : Output =>
    val prefix = "--@test@"
    val qs = List(
      s"${prefix}select e, f, g, h from a where b = ?;",
      s"${prefix}select e, f, g, h from a where c = ?;",
      s"${prefix}select e, f, g, h from a where d in (?,?);" )
    qs.foreach { q =>
      output.write( q )
      output.write( "\n" )
    }

    val queries = QueryLoader.load( new File( "queries.txt" ) )

    queries.map( _.asText ) should equal (qs.map(_.stripPrefix(prefix)))
  }
}
