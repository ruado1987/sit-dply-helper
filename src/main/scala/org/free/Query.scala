package org.free

import net.noerd.prequel._
import ResultSetRowImplicits._

abstract class Query( text : String, val columns : Array[ String ] ) {

  def asText = text

  def execute( database : DatabaseConfig ) : Seq[ QueryResult ] = {
    database.transaction { tx =>
      tx.select( asText ) { row =>
        buildQueryResult( row )
      }
    }
  }

  def buildQueryResult( row : ResultSetRow ) : QueryResult
}

object Query {

  val select = "select"

  def apply( text : String ) = {
    val from = text.indexOf( select ) + select.length
    val to = text.indexOf( "from" )
    val columns : Array[ String ] = text.slice( from, to ).split( "," ).map( _.trim() )

    columns.size match {
      case 4 => SimpleQuery( text, columns )
      case 11 => ExtendedQuery( text, columns )
    }
  }
}

case class SimpleQuery private[ free ] ( text : String, override val columns : Array[ String ] )
  extends Query( text, columns ) {

  def buildQueryResult( row : ResultSetRow ) =
    new StandardVehicleQueryResult( row, row, row, row )
}

case class ExtendedQuery private[ free ] ( text : String, override val columns : Array[ String ] )
  extends Query( text, columns ) {

  def buildQueryResult( row : ResultSetRow ) =
    new ExtendedVehicleQueryResult( row, row, row, row, row, row, row, row, row, row, row )
}
