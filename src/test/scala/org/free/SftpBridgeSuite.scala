package org.free

import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

import org.apache.commons.vfs2._

class SftpBridgeSuite extends FunSuite with MockitoSugar {

    test("access a remote dir on default server") {
        val fsm = mock[FileSystemManager]
        val f = mock[FileObject]
        val fn = mock[FileName]
        val dir = "/testdir"
        
        when(fsm.resolveFile(contains(dir), any(classOf[FileSystemOptions]))).thenReturn(f)
        when(f.getChildren()).thenReturn(Array[FileObject](f))
        when(f.getName()).thenReturn(fn)
        when(fn.getBaseName()).thenReturn("testfile")

        val bridge = SftpBridge(fsm)
        bridge.dir(dir) { f =>
            assert( f.name == "testfile" )
        }
    }
}
