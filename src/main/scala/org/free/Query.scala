package org.free

import net.noerd.prequel._
import ResultSetRowImplicits._

abstract class Query( text : String, val columns : Array[ String ], databaseProvider: DatabaseProvider )
	extends ExecutableQuery with ExcelSheetLike {
  
  private val regex = """(?s)[^@]*@([^@]+)@(.+)""".r
  private val regex( qname, query ) = text  
  
  val sheetName = name
  
  def asText = query
  def name : String = qname
  def header = columns
  def data = execute(databaseProvider.database)
  
  def buildQueryResult( row : ResultSetRow ) : QueryResult
  
  def execute(implicit database: DatabaseConfig) = {
    database.transaction { tx =>
      tx.select( asText ) { row =>
        buildQueryResult( row )
      }
    }
  }
}

object Query {

  val select = "select"

  def apply( text : String )(implicit databaseProvider: DatabaseProvider) = {
    val from = text.indexOf( select ) + select.length
    val to = text.indexOf( "from" )
    val columns : Array[ String ] = text.slice( from, to ).split( "," ).map( _.trim() )

    columns.size match {
      case 4 => SimpleQuery( text, columns,databaseProvider )
      case 11 => ExtendedQuery( text, columns, databaseProvider )
    }
  }
}

case class SimpleQuery private[ free ] ( text : String, override val columns : Array[ String ],databaseProvider: DatabaseProvider )
  extends Query( text, columns, databaseProvider ) {

  def buildQueryResult( row : ResultSetRow ) =
    new StandardVehicleQueryResult( row, row, row, row )
}

case class ExtendedQuery private[ free ] ( text : String, override val columns : Array[ String ],databaseProvider: DatabaseProvider )
  extends Query( text, columns, databaseProvider ) {

  def buildQueryResult( row : ResultSetRow ) =
    new ExtendedVehicleQueryResult( row, row, row, row, row, row, row, row, row, row, row )
}

trait ExecutableQuery {
  
  def execute(implicit database: DatabaseConfig) : Seq[QueryResult]  
}

trait DatabaseProvider {
  
  def database: DatabaseConfig
}
