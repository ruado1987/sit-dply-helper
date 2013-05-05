package org.free

import java.io.{ OutputStream }

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{ Workbook }

import collection.mutable.ListBuffer

trait WorkbookLike[ T <: ExcelSheetLike ] extends WorkBookProvider {

  private val sheets : ListBuffer[ T ] = ListBuffer[ T ]()

  def addSheet( sheet : T ) {
    sheets += sheet
  }

  def save( stream : OutputStream ) {
    val workbook : Workbook = createWorkbook
    sheets.foreach( _.addTo( workbook ) )

    workbook.write( stream )
  }

}

trait WorkBookProvider {
  def createWorkbook : Workbook
}

trait XlsWorkBookProvider extends WorkBookProvider {
  def createWorkbook : Workbook = new HSSFWorkbook
}