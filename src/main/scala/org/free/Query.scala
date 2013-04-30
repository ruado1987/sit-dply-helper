package org.free

import net.noerd.prequel._
import ResultSetRowImplicits._

abstract class Query( text : String ) {

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

  def apply( text : String ) = {
    val from = text.indexOf( "select" )+"select".length
    val to = text.indexOf( "from" )

    text.slice( from, to ).count( _ == ',' ) match {
      case 3 => new SimpleQuery( text )
      case 10 => new ExtendedQuery( text )
    }
  }
}

case class SimpleQuery private[ free ] ( text : String ) extends Query( text ) {

  def buildQueryResult( row : ResultSetRow ) =
    new StandardVehicleQueryResult( row, row, row, row )
}

case class ExtendedQuery private[ free ] ( text : String ) extends Query( text ) {
  def buildQueryResult( row : ResultSetRow ) =
    new ExtendedVehicleQueryResult( row, row, row, row, row, row, row, row, row, row, row )
}
