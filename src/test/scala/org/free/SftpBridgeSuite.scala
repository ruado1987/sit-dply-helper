package org.free

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers
import org.apache.commons.vfs2._

class SftpBridgeSuite extends FunSuite with BeforeAndAfter with MockitoSugar {

  val fsm = mock[ FileSystemManager ]
  val remoteFile = mock[ FileObject ]
  val pwdAuth = new PasswordAuthentication( "harry", "password" )
  val conUrl = "sftp://user@server"

  var bridge : SftpBridge = _

  before {
    bridge = SftpBridge( fsm, conUrl, pwdAuth )

    val fileName = mock[ FileName ]
    when( remoteFile.getChildren() ).thenReturn( Array[ FileObject ]( remoteFile ) )
    when( remoteFile.getName() ).thenReturn( fileName )
    when( fileName.getBaseName() ).thenReturn( "testfile" )
    when( fsm.resolveFile( Matchers.eq( conUrl ), Matchers.any( classOf[ FileSystemOptions ] ) ) )
      .thenReturn( remoteFile )
  }

  test( "access a remote dir on a specified server" ) {
    when( remoteFile.getChild( Matchers.anyString() ) ).thenReturn( remoteFile )
    when( remoteFile.getChildren() ).thenReturn( Array( remoteFile ) )

    bridge.dir( "/testdir" ) { f =>
      assert( f.name == "testfile" )
    }
  }

  test( "push local file to server" ) {
    val localFile = mock[ FileObject ]
    val localPath = "/localfolder/testfile"
    val remotePath = "/remotefolder/testfile"

    when( fsm.resolveFile( Matchers.matches( localPath ) ) )
      .thenReturn( localFile )
    when( remoteFile.resolveFile( Matchers.contains( remotePath ) ) )
      .thenReturn( remoteFile )

    bridge.push( localPath, remotePath )

    verify( remoteFile ).copyFrom( Matchers.same( localFile ), Matchers.any( classOf[ FileTypeSelector ] ) )
  }
}
