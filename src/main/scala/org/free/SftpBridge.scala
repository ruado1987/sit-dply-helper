package org.free

import java.io.{ File }
import org.apache.commons.vfs2._
import com.jcraft.jsch._

class SftpBridge private ( val vfsManager : FileSystemManager,
                           val connectionUrl : String,
                           val authType : AuthenticationType ) {

  lazy val urlRegex = """^(?:[^\:]+)\:/{2}([^\:]+)\@([a-zA-Z.\-0-9]+)(?:\:([0-9]+))?/(?:\S+)?$""".r
  lazy val urlRegex( user, host, port ) = connectionUrl

  lazy val remoteHomeDir = vfsManager.resolveFile( connectionUrl, authType.toFileSystemOptions )

  def dir( dirname : String )( f : VirtualFile => Unit ) {
    val subDir = remoteHomeDir.getChild( dirname )
    for ( child <- subDir.getChildren() ) {
      f( new VirtualFile( child ) )
    }
  }

  def push( localPath : String, remotePath : String ) {
    val localFile = vfsManager.resolveFile( localPath )
    val remoteFile = remoteHomeDir.resolveFile( remotePath )

    remoteFile.copyFrom( localFile, new FileTypeSelector( FileType.FILE ) )
  }

  def exec( command : String ) : Int = {
    val session = authType.authenticate( host, if ( port == null ) 22 else port.toInt )
    val channel : ChannelExec = session.openChannel( "exec" ).asInstanceOf[ ChannelExec ]

    try {
      channel.setCommand( command )
      channel.setInputStream( null )
      channel.connect()
      // wait until the command execution completed
      while ( !channel.isClosed() ) {}

      return channel.getExitStatus()
    } finally {
      channel.disconnect()
      session.disconnect()
    }
  }
}

object SftpBridge {

  lazy val fsManager = VFS.getManager()

  def apply( vfsm : FileSystemManager = fsManager, conUrl : String, authType : AuthenticationType ) =
    new SftpBridge( vfsm, conUrl, authType )

  implicit def stringToVirtualFile( path : String ) : VirtualFile = {
    new VirtualFile( fsManager.resolveFile( path ) )
  }
}
