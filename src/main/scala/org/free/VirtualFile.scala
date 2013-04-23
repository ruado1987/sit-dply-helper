package org.free

import org.apache.commons.vfs2._

sealed class VirtualFile protected[ free ] ( val file : FileObject ) {

  def name = file.getName.getBaseName

  def copyTo( destFile : VirtualFile ) {
    FileUtil.copyContent( file, destFile.file )
  }
}
