package org.free

import org.scalatest._

import net.noerd.prequel._
import SQLFormatterImplicits._

class QuerySuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  implicit def stringArray2Formattables( strings : Array[ String ] ) = {
    for ( string <- strings ) yield string2Formattable( string )
  }

  val config = DatabaseConfig(
    driver = "org.h2.Driver",
    jdbcURL = "jdbc:h2:mem:testDB" )

  val row = Array(
    "123", "s1234", "10", "E",
    "2013-01-12", "2013-03-20",
    "456", "S777", "S888", "A", "N" )

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

  test( "execute standard query" ) {
    val results =
      Query( """select veh_sys_num, veh_num,
                        body_cd, class_cd from wr_veh_mas""" )
        .execute( config )

    assert( results.head.mkString ==
      row( 0 ).padTo( 20, ' ' ) +
      row( 1 ).padTo( 20, ' ' ) +
      row( 2 ).padTo( 5, ' ' ) +
      row( 3 ).padTo( 5, ' ' ) )
  }

  test( "execute extended query" ) {
    val results =
      Query( """select veh_sys_num, veh_num, body_cd,
                        class_cd, temp_start_dt, perm_out_dt,
                        acc_sys_num, seller_id, buyer_id,
                        buyer_type, dealer from wr_veh_mas""" )
        .execute( config )

    assert( results.head.mkString ==
      row( 0 ).padTo( 20, ' ' ) +
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
}