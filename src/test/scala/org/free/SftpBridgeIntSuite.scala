package org.free

import java.io.File
import java.util.EnumSet

import scala.collection.JavaConversions._

import org.apache.sshd.SshServer
import org.apache.sshd.server.shell.ProcessShellFactory
import org.apache.sshd.server.auth.UserAuthNone
import org.apache.sshd.server.command.ScpCommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.sftp.SftpSubsystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite

class SftpBridgeIntSuite extends FunSuite with BeforeAndAfterAll {

  var sshd : SshServer = _
  var bridge : SftpBridge = _

  val port = 2222

  override def beforeAll {
    val userAuthFactories = List( new UserAuthNone.Factory() )
    val namedFactoryList = List( new SftpSubsystem.Factory() )
    val pwdAuth = new PasswordAuthentication( "harry", "test" )
    val shellFactory = new ProcessShellFactory(Array[String]( "cmd.exe" ),
                 EnumSet.of(ProcessShellFactory.TtyOptions.Echo, ProcessShellFactory.TtyOptions.ICrNl, ProcessShellFactory.TtyOptions.ONlCr))

    bridge = SftpBridge( conUrl = s"sftp://harry@localhost:$port/", authType = pwdAuth )
    sshd = SshServer.setUpDefaultServer()

    sshd.setPort( port )
    sshd.setKeyPairProvider( new SimpleGeneratorHostKeyProvider( "hostkey.ser" ) )
    sshd.setUserAuthFactories( userAuthFactories )
    sshd.setCommandFactory( new ScpCommandFactory() )
    sshd.setSubsystemFactories( namedFactoryList )
    sshd.setShellFactory( shellFactory )
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

  test( "execute command" ) {
/*    val pwdAuth = new PublicKeyAuthentication(
                        "twsapp01",
                        getClass.getResource("/prvkey.ppk").toURI.getPath )

    bridge = SftpBridge( conUrl = s"sftp://twsapp01@172.20.1.79:22/", authType = pwdAuth )*/
    assert( bridge.exec( "dir" ) == 0 )
  }

}
