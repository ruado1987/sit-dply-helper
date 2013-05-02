package org.free

import java.io.{ File }

import org.scalatest._
import scalax.io._
import scalax.file._

class QueryLoaderSuite extends fixture.FunSuite {

  type FixtureParam = Output

  override def withFixture( test : OneArgTest ) {
    val filename = "queries.txt"
    val out : Output = Resource.fromFile( filename )

    Path( filename ).deleteIfExists()

    test( out )
  }

  test( "load file" ) { output : Output =>
    val qs = List(
      "select e, f, g, h from a where b = ?;",
      "select e, f, g, h from a where c = ?;",
      "select e, f, g, h from a where d in (?,?);" )
    qs.foreach { q =>
      output.write( q )
      output.write( "\n" )
    }

    val queries =
      QueryLoader.load( new File( "queries.txt" ) )

    assert( queries.map( _.asText ) == qs )
  }
}
