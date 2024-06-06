import com.sun.jna.{Memory, Pointer}
import org.cameron.cs.WindowsMemoryService
import zio.*
import zio.test.*
import zio.test.Assertion.*

object WindowsMemoryServiceSpec extends ZIOSpecDefault {

  def spec = suite("WindowsMemoryServiceSpec")(
    test("virtualAlloc should allocate memory") {
      for {
        ptr <- WindowsMemoryService.virtualAlloc(1024)
        _   <- WindowsMemoryService.virtualFree(ptr, 1024)
      } yield assert(ptr)(not(isNull))
    },
    test("moveMemory should copy memory correctly") {
      val value = 12345678L
      for {
        src           <- WindowsMemoryService.virtualAlloc(8)
        dest          <- WindowsMemoryService.virtualAlloc(8)
        _             <- ZIO.attempt(new Memory(Pointer.nativeValue(src)).setLong(0, value))
        _             <- WindowsMemoryService.moveMemory(dest, src, 8)
        copiedValue   =  new Memory(Pointer.nativeValue(dest)).getLong(0)
        _             <- WindowsMemoryService.virtualFree(src, 8)
        _             <- WindowsMemoryService.virtualFree(dest, 8)
      } yield assert(copiedValue)(equalTo(value))
    },
    test("zeroMemory should zero memory correctly") {
      for {
        ptr   <- WindowsMemoryService.virtualAlloc(8)
        _     <- ZIO.attempt(new Memory(Pointer.nativeValue(ptr)).setLong(0, 12345678L))
        _     <- WindowsMemoryService.zeroMemory(ptr, 8)
        value =  new Memory(Pointer.nativeValue(ptr)).getLong(0)
        _     <- WindowsMemoryService.virtualFree(ptr, 8)
      } yield assert(value)(equalTo(0L))
    },
    test("fillMemory should fill memory correctly") {
      for {
        ptr   <- WindowsMemoryService.virtualAlloc(8)
        _     <- WindowsMemoryService.fillMemory(ptr, 8, 0xFF.toByte)
        value =  (0 until 8).map(ptr.getByte(_).toInt & 0xFF).toArray
        _     <- WindowsMemoryService.virtualFree(ptr, 8)
      } yield assert(value)(equalTo(Array.fill(8)(0xFF)))
    }
  ).provideLayer(WindowsMemoryService.live)
}
