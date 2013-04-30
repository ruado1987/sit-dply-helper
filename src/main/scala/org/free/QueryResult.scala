package org.free

import java.util.{ Date }

trait QueryResult {
  def mkString : String
}

class StandardVehicleQueryResult(
  val vehicleSysNum : String,
  val vehicleNum : String,
  val vehicleBodyCd : String,
  val vehicleClassCd : String ) extends QueryResult {

  def mkString : String = {
    vehicleSysNum.padTo( 20, ' ' ) +
      vehicleNum.padTo( 20, ' ' ) +
      vehicleBodyCd.padTo( 5, ' ' ) +
      vehicleClassCd.padTo( 5, ' ' )
  }
}

class ExtendedVehicleQueryResult(
  val sysNum : String,
  val num : String,
  val bodyCd : String,
  val classCd : String,
  val tempStartDt : Date,
  val permoutDt : Date,
  val accSysNum : String,
  val sellerId : String,
  val buyerId : String,
  val buyerType : String,
  val dealer : String ) extends StandardVehicleQueryResult( sysNum, num, bodyCd, classCd )
  with DateFormatter {

  override val pattern = "yyyy-MM-dd"

  override def mkString : String = {
    super.mkString +
      format( tempStartDt ).padTo( 15, ' ' ) +
      format( permoutDt ).padTo( 15, ' ' ) +
      accSysNum.padTo( 20, ' ' ) +
      sellerId.padTo( 10, ' ' ) +
      buyerId.padTo( 10, ' ' ) +
      buyerType.padTo( 5, ' ' ) +
      dealer.padTo( 2, ' ' )
  }
}

trait DateFormatter {

  import java.text.SimpleDateFormat

  val pattern : String

  def format( date : Date ) = {
    val df = new SimpleDateFormat( pattern )
    df format date
  }
}
