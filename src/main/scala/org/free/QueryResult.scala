package org.free

import java.util.{ Date }

import org.apache.poi.ss.usermodel.Row

trait QueryResult {
  def mkString : String
  def populateRow( excelRow : Row )
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

  override def populateRow( excelRow : Row ) {
    excelRow.createCell( 0 ).setCellValue( vehicleSysNum )
    excelRow.createCell( 1 ).setCellValue( vehicleNum )
    excelRow.createCell( 2 ).setCellValue( vehicleBodyCd )
    excelRow.createCell( 3 ).setCellValue( vehicleClassCd )
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

trait DateFormatter {

  import org.joda.time.format.{DateTimeFormatter,DateTimeFormat}

  val pattern : String
  lazy val formatter = DateTimeFormat.forPattern(pattern)

  def format( date : Date ) = {
    formatter print date.getTime
  }
}

class QueryResultToExcelConverter( query : Query, results : Seq[ QueryResult ] ) extends ExcelSheetLike {

  import org.apache.poi.hssf.usermodel._
  import java.io.{OutputStream}

  def header = query.columns
  def data = results

  def save( stream : OutputStream ) {
    val wb = new HSSFWorkbook()
    addTo(wb)

    wb.write( stream )
  }
}
