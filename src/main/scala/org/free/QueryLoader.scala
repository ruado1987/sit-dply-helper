package org.free

import java.io.{ File }

import language.postfixOps
import collection._
import collection.generic._
import collection.mutable.{ Builder, ArrayBuffer }

import scalax.io._
import Line.Terminators.Custom

object QueryLoader {

  def load( file : File ) : Queries = {
    val rsrc = Resource.fromFile( file )
    val lines = rsrc.lines( terminator = Custom( ";" ), includeTerminator = true )

    Queries( lines.view.filter( _.contains( "select" ) ) map ( line => Query( line.trim() ) ) toArray )
  }
}

class Queries private ( qs : Query* )
  extends Traversable[ Query ]
  with TraversableLike[ Query, Queries ] {

  def foreach[ U ]( f : Query => U ) = qs.toSeq.foreach( f )

  override def newBuilder : Builder[ Query, Queries ] = Queries.newBuilder
}

object Queries {

  def apply( qs : Array[ Query ] ) = new Queries( qs : _* )

  def newBuilder : Builder[ Query, Queries ] =
    new ArrayBuffer[ Query ] mapResult ( x => new Queries( x : _* ) )

  implicit def canBuildFrom : CanBuildFrom[ Queries, Query, Queries ] =
    new CanBuildFrom[ Queries, Query, Queries ] {
      def apply() : Builder[ Query, Queries ] = newBuilder
      def apply( from : Queries ) : Builder[ Query, Queries ] = newBuilder
    }
}
