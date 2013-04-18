package org.free

import java.io.{File}
import org.apache.commons.vfs2._
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder

class SftpBridge private(val vfsManager: FileSystemManager, val connectionUrl: String) {

    lazy val builder = SftpFileSystemConfigBuilder.getInstance()
    
    def sftpOptions = {
        val prvKey = new File(getClass.getResource("/prvkey.ppk").getPath())
        val fsOptions = new FileSystemOptions()
        builder.setStrictHostKeyChecking(fsOptions, "no");
        builder.setIdentities(fsOptions, Array[File](prvKey))
        
        fsOptions
    }

    def dir(dirname: String)(f: RemoteFile => Unit) {
        val remoteURL = connectionUrl + dirname
        val remoteFileObject = vfsManager.resolveFile(remoteURL, sftpOptions)
        for ( child <- remoteFileObject.getChildren() ) {
            f(new RemoteFile(child))
        }
    }
}

object SftpBridge {

    lazy val fsManager = VFS.getManager()

    def apply(vfsm: FileSystemManager = fsManager, conUrl: String = "sftp://wasuser@172.20.1.79:22") =
        new SftpBridge(vfsm, conUrl)
        
    implicit def stringToFileObject(path: String) : FileObject = {
        fsManager.resolveFile(path)
    }
}

