package org.free

import org.scalatest._
import mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers.{ eq => eql, _ }
import org.apache.commons.vfs2._
import com.jcraft.jsch._

abstract class SftpBridgeSuite extends fixture.FunSuite with MockitoSugar {

  type FixtureParam = SftpBridge

  val fsm = mock[ FileSystemManager ]
  val remoteFile = mock[ FileObject ]
  val auth = mock[ AuthenticationType ]

  /* abstract fields */
  val conUrl : String
  val port : Int

  override def withFixture( test : OneArgTest ) {
    val fileName = mock[ FileName ]
    when( remoteFile.getChildren() ).thenReturn( Array( remoteFile ) )
    when( remoteFile.getName() ).thenReturn( fileName )
    when( fileName.getBaseName() ).thenReturn( "testfile" )
    when( fsm.resolveFile( eql( conUrl ), any( classOf[ FileSystemOptions ] ) ) )
      .thenReturn( remoteFile )

    test( SftpBridge( fsm, conUrl, auth ) )
  }

  test( "access a remote dir on a specified server" ) { bridge : SftpBridge =>
    when( remoteFile.getChild( anyString() ) ).thenReturn( remoteFile )
    when( remoteFile.getChildren() ).thenReturn( Array( remoteFile ) )

    bridge.dir( "/testdir" ) { f =>
      assert( f.name == "testfile" )
    }
  }

  test( "push local file to server" ) { bridge : SftpBridge =>
    val localFile = mock[ FileObject ]
    val localPath = "/localfolder/testfile"
    val remotePath = "/remotefolder/testfile"

    when( fsm.resolveFile( matches( localPath ) ) )
      .thenReturn( localFile )
    when( remoteFile.resolveFile( contains( remotePath ) ) )
      .thenReturn( remoteFile )

    bridge.push( localPath, remotePath )

    verify( remoteFile ).copyFrom( same( localFile ), any( classOf[ FileTypeSelector ] ) )
  }

  test( "execute remote command against a server" ) { bridge : SftpBridge =>
    val session = mock[ Session ]
    val channel = mock[ ChannelExec ]

    when( auth.authenticate( eql( "server" ), eql( port ) ) )
      .thenReturn( session )
    when( session.openChannel( eql( "exec" ) ) )
      .thenReturn( channel )
    when( channel.isClosed() )
      .thenReturn( false, false, false, true )
    when( channel.getExitStatus() )
      .thenReturn( 0 )

    assert( bridge.exec( "echo test" ) == 0 )
  }
}

case class DefaultPortSuite() extends SftpBridgeSuite {
  override val conUrl = "sftp://user@server:22/"
  override val port = 22
}
case class SpecifiedPortSuite() extends SftpBridgeSuite {
  override val conUrl = "sftp://user@server:2222/"
  override val port = 2222
}
