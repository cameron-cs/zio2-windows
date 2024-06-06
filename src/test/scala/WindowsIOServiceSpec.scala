import com.sun.jna.Memory
import org.cameron.cs.WindowsIOService
import zio.*
import zio.test.*
import zio.test.Assertion.*

object WindowsIOServiceSpec extends ZIOSpecDefault {

  def spec = suite("WindowsIOServiceSpec")(
    test("createFile and closeHandle should work correctly") {
      for {
        handle <- WindowsIOService.createFile("C:\\temp\\testfile", 0x40000000, 0, 1, 0)
        result <- WindowsIOService.closeHandle(handle)
      } yield assert(result)(isTrue)
    },
    test("read and write should work correctly") {
      val value = 12345678L
      for {
        handle     <- WindowsIOService.createFile("C:\\temp\\testfile", 0x40000000 | 0x80000000, 0, 1, 0)
        source     =  new Memory(8)
        _          =  source.setLong(0, value)
        _          <- WindowsIOService.writeFile(handle, source, 8)
        _          <- WindowsIOService.setFilePointer(handle, 0, 0)
        readBuffer =  new Memory(8)
        _          <- WindowsIOService.readFile(handle, readBuffer, 8)
        readValue  =  readBuffer.getLong(0)
        _          <- WindowsIOService.closeHandle(handle)
      } yield assert(readValue)(equalTo(value))
    }
  ).provideLayer(WindowsIOService.live)
}
