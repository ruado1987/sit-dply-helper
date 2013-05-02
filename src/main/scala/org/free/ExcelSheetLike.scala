package org.free

import java.io._

import scala.language.reflectiveCalls

import org.apache.poi.ss.usermodel.IndexedColors._
import org.apache.poi.ss.usermodel.CellStyle._
import org.apache.poi.ss.usermodel.Font._
import org.apache.poi.hssf.usermodel._

trait ExcelSheetLike {

  type RowData = { def populateRow( row : HSSFRow ) : Unit }

  def header : Array[ String ]
  def data : Seq[ RowData ]

  def save( stream : OutputStream ) {
    val wb = new HSSFWorkbook()
    val sheet = wb.createSheet( "1" )
    val headerRow = sheet.createRow( 0 )

    val font = wb.createFont()
    font.setBoldweight( BOLDWEIGHT_BOLD )

    val style = wb.createCellStyle()
    style.setFillPattern( SOLID_FOREGROUND )
    style.setFillForegroundColor( LIGHT_YELLOW.getIndex )
    style.setBorderBottom( BORDER_THIN )
    style.setBottomBorderColor( BLACK.getIndex )
    style.setBorderLeft( BORDER_THIN )
    style.setLeftBorderColor( BLACK.getIndex )
    style.setBorderRight( BORDER_THIN )
    style.setRightBorderColor( BLACK.getIndex )
    style.setBorderTop( BORDER_THIN )
    style.setTopBorderColor( BLACK.getIndex )
    style.setFont( font )

    for ( ( h, idx ) <- header.zipWithIndex ) {
      val cell = headerRow.createCell( idx )
      cell.setCellValue( h.toUpperCase )
      cell.setCellStyle( style )
      sheet.autoSizeColumn( idx )
    }

    for ( ( qr, idx ) <- data.zipWithIndex ) {
      qr.populateRow( sheet.createRow( idx + 1 ) )
    }

    wb.write( stream )
  }
}
