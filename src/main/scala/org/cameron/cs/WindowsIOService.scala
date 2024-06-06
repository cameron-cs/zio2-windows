package org.cameron.cs

import com.sun.jna.{Pointer, Memory}
import zio._

trait WindowsIOService {
  def createFile(pathname: String, access: Int, shareMode: Int, creationDisposition: Int, flagsAndAttributes: Int): Task[Pointer]
  def closeHandle(handle: Pointer): Task[Boolean]
  def readFile(handle: Pointer, buffer: Pointer, bytesToRead: Int): Task[Int]
  def writeFile(handle: Pointer, buffer: Pointer, bytesToWrite: Int): Task[Int]
  def setFilePointer(handle: Pointer, distance: Int, moveMethod: Int): Task[Pointer]
}

object WindowsIOService {

  def createFile(pathname: String, access: Int, shareMode: Int, creationDisposition: Int, flagsAndAttributes: Int): ZIO[WindowsIOService, Throwable, Pointer] =
    ZIO.serviceWithZIO[WindowsIOService](_.createFile(pathname, access, shareMode, creationDisposition, flagsAndAttributes))

  def closeHandle(handle: Pointer): ZIO[WindowsIOService, Throwable, Boolean] =
    ZIO.serviceWithZIO[WindowsIOService](_.closeHandle(handle))

  def readFile(handle: Pointer, buffer: Pointer, bytesToRead: Int): ZIO[WindowsIOService, Throwable, Int] =
    ZIO.serviceWithZIO[WindowsIOService](_.readFile(handle, buffer, bytesToRead))

  def writeFile(handle: Pointer, buffer: Pointer, bytesToWrite: Int): ZIO[WindowsIOService, Throwable, Int] =
    ZIO.serviceWithZIO[WindowsIOService](_.writeFile(handle, buffer, bytesToWrite))

  def setFilePointer(handle: Pointer, distance: Int, moveMethod: Int): ZIO[WindowsIOService, Throwable, Pointer] =
    ZIO.serviceWithZIO[WindowsIOService](_.setFilePointer(handle, distance, moveMethod))

  val live: ULayer[WindowsIOService] = ZLayer.succeed(new WindowsIOService {
    override def createFile(pathname: String, access: Int, shareMode: Int, creationDisposition: Int, flagsAndAttributes: Int): Task[Pointer] =
      ZioWindowsIOJna.createFile(pathname, access, shareMode, creationDisposition, flagsAndAttributes)

    override def closeHandle(handle: Pointer): Task[Boolean] =
      ZioWindowsIOJna.closeHandle(handle)

    override def readFile(handle: Pointer, buffer: Pointer, bytesToRead: Int): Task[Int] =
      ZioWindowsIOJna.readFile(handle, buffer, bytesToRead)

    override def writeFile(handle: Pointer, buffer: Pointer, bytesToWrite: Int): Task[Int] =
      ZioWindowsIOJna.writeFile(handle, buffer, bytesToWrite)

    override def setFilePointer(handle: Pointer, distance: Int, moveMethod: Int): Task[Pointer] =
      ZioWindowsIOJna.setFilePointer(handle, distance, moveMethod)
  })


  object Mode {
    val GENERIC_READ = 0x80000000
    val GENERIC_WRITE = 0x40000000
    val GENERIC_EXECUTE = 0x20000000
    val GENERIC_ALL = 0x10000000
    val FILE_SHARE_READ = 0x00000001
    val FILE_SHARE_WRITE = 0x00000002
    val FILE_SHARE_DELETE = 0x00000004
    val CREATE_NEW = 1
    val CREATE_ALWAYS = 2
    val OPEN_EXISTING = 3
    val OPEN_ALWAYS = 4
    val TRUNCATE_EXISTING = 5
    val FILE_ATTRIBUTE_READONLY = 0x00000001
    val FILE_ATTRIBUTE_HIDDEN = 0x00000002
    val FILE_ATTRIBUTE_SYSTEM = 0x00000004
    val FILE_ATTRIBUTE_DIRECTORY = 0x00000010
    val FILE_ATTRIBUTE_ARCHIVE = 0x00000020
    val FILE_ATTRIBUTE_DEVICE = 0x00000040
    val FILE_ATTRIBUTE_NORMAL = 0x00000080
    val FILE_ATTRIBUTE_TEMPORARY = 0x00000100
    val FILE_ATTRIBUTE_SPARSE_FILE = 0x00000200
    val FILE_ATTRIBUTE_REPARSE_POINT = 0x00000400
    val FILE_ATTRIBUTE_COMPRESSED = 0x00000800
    val FILE_ATTRIBUTE_OFFLINE = 0x00001000
    val FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x00002000
    val FILE_ATTRIBUTE_ENCRYPTED = 0x00004000
  }

  object Permission {
    val FILE_GENERIC_READ = 0x120089
    val FILE_GENERIC_WRITE = 0x120116
    val FILE_GENERIC_EXECUTE = 0x1200A0
    val FILE_ALL_ACCESS = 0x1F01FF
    val DELETE = 0x00010000
    val READ_CONTROL = 0x00020000
    val WRITE_DAC = 0x00040000
    val WRITE_OWNER = 0x00080000
    val SYNCHRONIZE = 0x00100000
  }

  object Indicator {
    val FILE_BEGIN = 0
    val FILE_CURRENT = 1
    val FILE_END = 2
  }
}
