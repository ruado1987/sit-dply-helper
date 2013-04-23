package org.free

import java.io.File
import java.util.EnumSet

import scala.collection.JavaConversions._

import org.apache.sshd._
import common.util._
import server._
import shell.ProcessShellFactory
import auth.UserAuthNone
import command.ScpCommandFactory
import keyprovider.SimpleGeneratorHostKeyProvider
import sftp.SftpSubsystem
import org.scalatest._

class SftpBridgeIntSuite extends FunSuite with BeforeAndAfterAll {

  var sshd : SshServer = _
  var bridge : SftpBridge = _

  val port = 2222

  object SimpleCommandFactory extends CommandFactory {
    override def createCommand( command : String ) : Command = {
      val winCmd = if ( OsUtils.isUNIX() ) command else s"cmd.exe /C $command"
      return new ProcessShellFactory( winCmd.split( " " ) ).create()
    }
  }

  override def beforeAll {
    val userAuthFactories = List( new UserAuthNone.Factory() )
    val namedFactoryList = List( new SftpSubsystem.Factory() )
    val pwdAuth = new PasswordAuthentication( "harry", "test" )

    bridge = SftpBridge( conUrl = s"sftp://harry@localhost:$port/", authType = pwdAuth )
    sshd = SshServer.setUpDefaultServer()

    sshd.setPort( port )
    sshd.setKeyPairProvider( new SimpleGeneratorHostKeyProvider( "hostkey.ser" ) )
    sshd.setUserAuthFactories( userAuthFactories )
    sshd.setCommandFactory( new ScpCommandFactory( SimpleCommandFactory ) )
    sshd.setSubsystemFactories( namedFactoryList )
    sshd.start()
  }

  override def afterAll {
    sshd.stop()
  }

  test( "list remote directory content" ) {
    bridge.dir( "project" ) { f =>
      assert( f.name != null )
    }
  }

  test( "push local file to server" ) {
    withFile( new File( "project/test.txt" ) ) { f =>
      val pathToTestFile = getClass.getResource( "/test.txt" ).toURI.getPath
      bridge.push( pathToTestFile, "project/test.txt" )

      assert( f.exists() )
    }
  }

  def withFile( file : File )( f : File => Unit ) {
    file.delete()

    f( file )
  }

  test( "execute remote command" ) {
    val exitStatus = bridge.exec( "echo test" )
    assert( exitStatus == 0, s"Exit status is not zero: $exitStatus" )
  }

}
