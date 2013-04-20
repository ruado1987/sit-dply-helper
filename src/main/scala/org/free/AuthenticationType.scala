package org.free

import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.auth.StaticUserAuthenticator
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder
import java.io.File
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder

sealed abstract class AuthenticationType {
  def toFileSystemOptions: FileSystemOptions
}

case class PasswordAuthentication(username: String, password: String) extends AuthenticationType {

  def toFileSystemOptions: FileSystemOptions = {
    val auth = new StaticUserAuthenticator(null, username, password)
    val fsOps = new FileSystemOptions()
    
    DefaultFileSystemConfigBuilder.getInstance()
      .setUserAuthenticator(fsOps, auth)

    fsOps
  }
}

case class PublicKeyAuthentication(keyPath: String) extends AuthenticationType {
  
	def toFileSystemOptions: FileSystemOptions = {
        val fsOps = new FileSystemOptions()
	    val builder = SftpFileSystemConfigBuilder.getInstance()
        
	    builder.setStrictHostKeyChecking(fsOps, "no");
        builder.setIdentities(fsOps, Array[File](new File(keyPath)))
        
        fsOps
	}
}