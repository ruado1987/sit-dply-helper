package org.free

import org.scalatest._
import matchers._

import net.noerd.prequel._
import SQLFormatterImplicits._
import java.io.{ OutputStream, FileOutputStream }

class QuerySuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter with ShouldMatchers {

  implicit def stringArray2Formattables( strings : Array[ String ] ) = {
    for ( string <- strings ) yield string2Formattable( string )
  }

  implicit def config = DatabaseConfig(
                driver = "org.h2.Driver",
                jdbcURL = "jdbc:h2:mem:testDB" )
                
  implicit val h2databaseProvider = new DatabaseProvider {
    
    def database = config
  }           

  val row = Array(
              "123", "s1234", "10", "E",
              "2013-01-12", "2013-03-20",
              "456", "S777", "S888", "A", "N" )

  val sQuery = """--@favorite vehicles@
                        select veh_sys_num, veh_num,
                                body_cd, class_cd from wr_veh_mas"""

  val eQuery = """--@favorite vehicles@
                        select veh_sys_num, veh_num, body_cd,
                                class_cd, temp_start_dt, perm_out_dt,
                                acc_sys_num, seller_id, buyer_id,
                                buyer_type, dealer from wr_veh_mas"""

  override def beforeAll {
    config.transaction { tx =>
      tx.execute( """create table wr_veh_mas(
                      veh_sys_num varchar(20),
                      veh_num varchar(20),
                      body_cd varchar(5),
                      class_cd varchar(5),
                      temp_start_dt date,
                      perm_out_dt date,
                      acc_sys_num varchar(20),
                      seller_id varchar(10),
                      buyer_id varchar(10),
                      buyer_type varchar(5),
                      dealer char(1) );""" )
    }
  }

  before {
    config.transaction { tx =>
      tx.execute( "insert into wr_veh_mas values(?,?,?,?,?,?,?,?,?,?,?);", row : _* )
    }
  }

  after {
    config.transaction { tx =>
      tx.execute( "truncate table wr_veh_mas" )
    }
  }

  override def afterAll {
    config.transaction { tx =>
      tx.execute( "drop table wr_veh_mas" )
    }
  }

  test( "query columns" ) {
    val q = Query( sQuery )
    q.columns should equal( Array( "veh_sys_num", "veh_num", "body_cd", "class_cd" ) )
  }
  
  test("query name") {
    val q = Query( sQuery )
    q.name should equal ("favorite vehicles")
    q.columns should equal( Array( "veh_sys_num", "veh_num", "body_cd", "class_cd" ) )
  }

  test( "execute standard query" ) {
    val results = Query( sQuery ).execute

    results.head.mkString should equal( row( 0 ).padTo( 20, ' ' ) +
      row( 1 ).padTo( 20, ' ' ) +
      row( 2 ).padTo( 5, ' ' ) +
      row( 3 ).padTo( 5, ' ' ) )
  }

  test( "execute extended query" ) {
    val results = Query( eQuery ).execute

    results.head.mkString should equal (row( 0 ).padTo( 20, ' ' ) +
      row( 1 ).padTo( 20, ' ' ) +
      row( 2 ).padTo( 5, ' ' ) +
      row( 3 ).padTo( 5, ' ' ) +
      row( 4 ).padTo( 15, ' ' ) +
      row( 5 ).padTo( 15, ' ' ) +
      row( 6 ).padTo( 20, ' ' ) +
      row( 7 ).padTo( 10, ' ' ) +
      row( 8 ).padTo( 10, ' ' ) +
      row( 9 ).padTo( 5, ' ' ) +
      row( 10 ).padTo( 2, ' ' ) )
  }

  test( "convert simple query result to excel sheet" ) {
    val queries = Queries( Array(Query( sQuery )) )

    withOutputStream( "test.xls" ) { out =>
      queries.save( out )
    }
  }

  test( "convert extended query result to excel sheet" ) {
    val queries = Queries( Array(Query( eQuery )) )    

    withOutputStream( "test2.xls" ) { out =>
      queries.save( out )
    }
  }

  def withOutputStream( fileName : String )( f : OutputStream => Unit ) {
    val fout = new FileOutputStream( fileName )
    try {
      f( fout )
    } finally {
      fout.close()
    }
  }
}
