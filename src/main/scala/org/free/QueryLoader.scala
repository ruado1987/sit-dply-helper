package org.free

import java.io.{ File }
import scalax.io._
import Line.Terminators.Custom

object QueryLoader {
  def load( file : File ) : Queries = {
    val rsrc = Resource.fromFile( file )
    val lines = rsrc.lines( terminator = Custom( ";" ) )

    new Queries( lines.map { line => Query( line ) } toVector )
  }
}

class Queries private[ free ] ( qs : IndexedSeq[ Query ] ) {

  def apply( index : Int ) = qs( index )

  def length = qs.length

  def size = length
}

case class Query( text : String ) {
  def asText = text
}
