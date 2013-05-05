package org.free

import java.util.{ Date }

import org.apache.poi.ss.usermodel.Row

trait QueryResult {
  def mkString : String
  def populateRow( excelRow : Row )
}

class StandardVehicleQueryResult(
  sysNum : String,
  num : String,
  bodyCd : String,
  classCd : String ) extends QueryResult {

  def mkString : String = {
    sysNum.padTo( 20, ' ' ) +
      num.padTo( 20, ' ' ) +
      bodyCd.padTo( 5, ' ' ) +
      classCd.padTo( 5, ' ' )
  }

  override def populateRow( excelRow : Row ) {
    excelRow.createCell( 0 ).setCellValue( sysNum )
    excelRow.createCell( 1 ).setCellValue( num )
    excelRow.createCell( 2 ).setCellValue( bodyCd )
    excelRow.createCell( 3 ).setCellValue( classCd )
  }
}

class ExtendedVehicleQueryResult(
  sysNum : String,
  num : String,
  bodyCd : String,
  classCd : String,
  tempStartDt : Date,
  permoutDt : Date,
  accSysNum : String,
  sellerId : String,
  buyerId : String,
  buyerType : String,
  dealer : String ) extends StandardVehicleQueryResult( sysNum, num, bodyCd, classCd )
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

  override def populateRow( excelRow : Row ) {
    super.populateRow( excelRow )
    excelRow.createCell( 4 ).setCellValue( format( tempStartDt ) )
    excelRow.createCell( 5 ).setCellValue( format( permoutDt ) )
    excelRow.createCell( 6 ).setCellValue( accSysNum )
    excelRow.createCell( 7 ).setCellValue( sellerId )
    excelRow.createCell( 8 ).setCellValue( buyerId )
    excelRow.createCell( 9 ).setCellValue( buyerType )
    excelRow.createCell( 10 ).setCellValue( dealer )
  }
}
