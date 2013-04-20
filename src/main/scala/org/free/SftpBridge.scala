package org.free

import java.io.{ File }
import org.apache.commons.vfs2._

class SftpBridge private (val vfsManager: FileSystemManager,
  val connectionUrl: String, val authType: AuthenticationType) {

  lazy val remoteHomeDir = vfsManager.resolveFile(connectionUrl, authType.toFileSystemOptions)

  def dir(dirname: String)(f: VirtualFile => Unit) {
    val subDir = remoteHomeDir.getChild(dirname)
    for (child <- subDir.getChildren()) {
      f(new VirtualFile(child))
    }
  }

  def push(localPath: String, remotePath: String) {
    val localFile = vfsManager.resolveFile(localPath);
    val remoteFile = remoteHomeDir.resolveFile(remotePath)

    remoteFile.copyFrom(localFile, new FileTypeSelector(FileType.FILE))
  }
}

object SftpBridge {

  lazy val fsManager = VFS.getManager()

  def apply(vfsm: FileSystemManager = fsManager, conUrl: String, authType: AuthenticationType) =
    new SftpBridge(vfsm, conUrl, authType)

  implicit def stringToVirtualFile(path: String): VirtualFile = {
    new VirtualFile(fsManager.resolveFile(path))
  }
}

