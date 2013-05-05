package org.free

import java.io.{ File }

import language.postfixOps
import collection._
import generic._
import mutable.{ Builder, ArrayBuffer }

import scalax.io._
import Line.Terminators.Custom

import net.noerd.prequel._

object QueryLoader {

  private val semicolon = Custom( ";" )

  def load( file : File )( implicit databaseProvider : DatabaseProvider ) : Queries = {
    val rsrc = Resource.fromFile( file )
    val lines = rsrc.lines( terminator = semicolon, includeTerminator = true )

    Queries( lines.view.filter( _.contains( "select" ) ) map ( line => Query( line.trim() ) ) toArray )
  }
}

class Queries private ( qs : Query* )
  extends Traversable[ Query ]
  with TraversableLike[ Query, Queries ]
  with ExecutableQuery
  with WorkbookLike[ Query ]
  with XlsWorkBookProvider {

  def foreach[ U ]( f : Query => U ) = qs.toSeq.foreach( f )

  override def newBuilder : Builder[ Query, Queries ] = Queries.newBuilder

  def execute( implicit database : DatabaseConfig ) = {
    ( for ( query <- this ) yield query.execute( database ) ).view.flatMap( _.toList ).toSeq
  }
}

object Queries {

  def apply( qs : Array[ Query ] ) = {
    val queries = new Queries( qs : _* )
    for ( q <- qs ) queries.addSheet( q )

    queries
  }

  def newBuilder : Builder[ Query, Queries ] =
    new ArrayBuffer[ Query ] mapResult ( x => new Queries( x : _* ) )

  implicit def canBuildFrom : CanBuildFrom[ Queries, Query, Queries ] =
    new CanBuildFrom[ Queries, Query, Queries ] {
      def apply() : Builder[ Query, Queries ] = newBuilder
      def apply( from : Queries ) : Builder[ Query, Queries ] = newBuilder
    }
}
