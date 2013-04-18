package org.free

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

import org.apache.commons.vfs2._

class SftpBridgeSuite extends FunSuite with BeforeAndAfter with MockitoSugar {

    val fsm = mock[FileSystemManager]
    val f = mock[FileObject]

    before {
        val fn = mock[FileName]
        when(f.getChildren()).thenReturn(Array[FileObject](f))
        when(f.getName()).thenReturn(fn)
        when(fn.getBaseName()).thenReturn("testfile")
    }

    test("access a remote dir on default server") {
        val dir = "/testdir"
        val bridge = SftpBridge(fsm)

        when(fsm.resolveFile(contains(dir), any(classOf[FileSystemOptions])))
            .thenReturn(f)

        bridge.dir(dir) { f =>
            assert( f.name == "testfile" )
        }
    }
    
    test("access a remote dir on a specified server") {
        val conUrl = "sftp://user@server"
        val bridge = SftpBridge(fsm, conUrl)

        when(fsm.resolveFile(contains(conUrl), any(classOf[FileSystemOptions])))
            .thenReturn(f)

        bridge.dir("/testdir") { f =>
            assert( f.name == "testfile" )
        }
    }
    
    test("push local file to server") {
        val localFile = mock[FileObject]
        val bridge = SftpBridge(fsm)
        val localPath = "D:/testfile"
        val remotePath = "/remotefolder/testfile"

        when(fsm.resolveFile(matches(localPath)))
            .thenReturn(localFile)
        when(fsm.resolveFile(contains(remotePath), any(classOf[FileSystemOptions])))
            .thenReturn(f)

        bridge.push(localPath, remotePath)

        verify(f).copyFrom(same(localFile), any(classOf[FileTypeSelector]))
    }
}
