package org.free

import java.io.{File}
import org.apache.commons.vfs2._
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder

class SftpBridge private(val vfsManager: FileSystemManager, val connectionUrl: String) {

    lazy val builder = SftpFileSystemConfigBuilder.getInstance()
    
    def sftpOptions = {
        // URI doesnt encode whitespaces in file path.
        // If an encoded file path is passed to the File constructor,
        // it may throw exception because it is not aware of encoded file path
        val prvKey = new File(getClass.getResource("/prvkey.ppk").toURI.getPath)
        val fsOptions = new FileSystemOptions()
        builder.setStrictHostKeyChecking(fsOptions, "no");
        builder.setIdentities(fsOptions, Array[File](prvKey))
        
        fsOptions
    }

    def dir(dirname: String)(f: VirtualFile => Unit) {
        val remoteURL = connectionUrl + dirname
        val remoteFileObject = vfsManager.resolveFile(remoteURL, sftpOptions)
        for ( child <- remoteFileObject.getChildren() ) {
            f(new VirtualFile(child))
        }
    }

    def push(localPath: String, remotePath: String) {
        val localFile = vfsManager.resolveFile(localPath);
        val remoteFile = vfsManager.resolveFile(connectionUrl + remotePath, sftpOptions)

        remoteFile.copyFrom(localFile, new FileTypeSelector(FileType.FILE))
    }
}

object SftpBridge {

    lazy val fsManager = VFS.getManager()

    def apply(vfsm: FileSystemManager = fsManager, conUrl: String = "sftp://wasuser@172.20.1.79:22") =
        new SftpBridge(vfsm, conUrl)

    implicit def stringToVirtualFile(path: String) : VirtualFile = {
        new VirtualFile(fsManager.resolveFile(path))
    }
}

