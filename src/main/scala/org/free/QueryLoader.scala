package org.free

import java.io.{ File }
import scalax.io._
import Line.Terminators.Custom

import scala.collection._
import scala.collection.generic._
import scala.collection.mutable.{ Builder, ArrayBuffer }

object QueryLoader {
  def load( file : File ) : Queries = {
    val rsrc = Resource.fromFile( file )
    val lines = rsrc.lines( terminator = Custom( ";" ), includeTerminator = true )

    new Queries( ( lines.view.filter( _.contains( "select" ) )
      .map { line => Query( line.trim() ) } ).toArray : _* )
  }
}

class Queries( qs : Query* )
  extends Traversable[ Query ]
  with TraversableLike[ Query, Queries ] {

  def foreach[ U ]( f : Query => U ) = qs.toSeq.foreach( f )

  override def newBuilder : Builder[ Query, Queries ] = Queries.newBuilder
}

object Queries {

  def newBuilder : Builder[ Query, Queries ] =
    new ArrayBuffer[ Query ] mapResult ( x => new Queries( x : _* ) )

  implicit def canBuildFrom : CanBuildFrom[ Queries, Query, Queries ] =
    new CanBuildFrom[ Queries, Query, Queries ] {
      def apply() : Builder[ Query, Queries ] = newBuilder
      def apply( from : Queries ) : Builder[ Query, Queries ] = newBuilder
    }
}

case class Query( text : String ) {
  def asText = text
}
