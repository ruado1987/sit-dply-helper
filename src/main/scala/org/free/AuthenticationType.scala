package org.free

import java.io.File

import org.apache.commons.vfs2._
import auth.StaticUserAuthenticator
import impl.DefaultFileSystemConfigBuilder
import provider.sftp._

import com.jcraft.jsch._

sealed abstract class AuthenticationType {
  def toFileSystemOptions : FileSystemOptions
  def authenticate( host : String, port : Int ) : Session
}

case class PasswordAuthentication( username : String, password : String ) extends AuthenticationType {

  def toFileSystemOptions : FileSystemOptions = {
    val auth = new StaticUserAuthenticator( null, username, password )
    val fsOps = new FileSystemOptions()

    DefaultFileSystemConfigBuilder.getInstance()
      .setUserAuthenticator( fsOps, auth )

    fsOps
  }

  def authenticate( host : String, port : Int ) : Session = {
    SftpClientFactory.createConnection(
      host, port.toInt,
      username.toCharArray,
      password.toCharArray,
      toFileSystemOptions )
  }
}

case class PublicKeyAuthentication( username : String, keyPath : String ) extends AuthenticationType {

  def toFileSystemOptions : FileSystemOptions = {
    val fsOps = new FileSystemOptions()
    val builder = SftpFileSystemConfigBuilder.getInstance()

    builder.setStrictHostKeyChecking( fsOps, "no" );
    builder.setIdentities( fsOps, Array[ File ]( new File( keyPath ) ) )

    fsOps
  }

  def authenticate( host : String, port : Int ) : Session = {
    SftpClientFactory.createConnection(
      host, port.toInt,
      username.toCharArray,
      null,
      toFileSystemOptions )
  }
}
