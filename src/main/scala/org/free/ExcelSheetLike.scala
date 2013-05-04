package org.free

import scala.language.reflectiveCalls

import org.apache.poi.ss.usermodel._
import IndexedColors._
import CellStyle._
import Font._

trait ExcelSheetLike {

  type RowData = { def populateRow( row : Row ) : Unit }

  def header : Array[ String ]

  def data : Seq[ RowData ]

  val sheetName : String

  def addTo( wb : Workbook ) {
    val sheet = wb.createSheet( sheetName )

    addHeaderTo( sheet, createHeaderStyle( wb ) )
    addBodyTo( sheet )
  }

  private def createHeaderStyle( wb : Workbook ) : CellStyle = {
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

    style
  }

  private def addHeaderTo( sheet : Sheet, style : CellStyle ) {
    val headerRow = sheet.createRow( 0 )
    for ( ( h, idx ) <- header.zipWithIndex ) {
      val cell = headerRow.createCell( idx )
      cell.setCellValue( h.toUpperCase )
      cell.setCellStyle( style )

      sheet.autoSizeColumn( idx )
    }
  }

  private def addBodyTo( sheet : Sheet ) {
    for ( ( qr, idx ) <- data.zipWithIndex ) {
      qr.populateRow( sheet.createRow( idx + 1 ) )
    }
  }

}
