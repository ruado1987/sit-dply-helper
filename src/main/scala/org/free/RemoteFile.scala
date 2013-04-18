package org.free

import org.apache.commons.vfs2._

sealed class RemoteFile protected[free](val file: FileObject) {

    def name = file.getName.getBaseName
    
    def copyTo(destFile: FileObject) {
        FileUtil.copyContent(file, destFile)
    }
}
