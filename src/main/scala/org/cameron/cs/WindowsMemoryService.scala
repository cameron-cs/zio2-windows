package org.cameron.cs

import com.sun.jna.{Memory, Pointer}
import zio._

trait WindowsMemoryService {
  def virtualAlloc(size: Long): Task[Pointer]
  def virtualFree(ptr: Pointer, size: Long): Task[Boolean]
  def moveMemory(dest: Pointer, src: Pointer, size: Long): Task[Unit]
  def zeroMemory(ptr: Pointer, size: Long): Task[Unit]
  def fillMemory(ptr: Pointer, size: Long, fill: Byte): Task[Unit]
}

object WindowsMemoryService {
  def virtualAlloc(size: Long): ZIO[WindowsMemoryService, Throwable, Pointer] =
    ZIO.serviceWithZIO[WindowsMemoryService](_.virtualAlloc(size))

  def virtualFree(ptr: Pointer, size: Long): ZIO[WindowsMemoryService, Throwable, Boolean] =
    ZIO.serviceWithZIO[WindowsMemoryService](_.virtualFree(ptr, size))

  def moveMemory(dest: Pointer, src: Pointer, size: Long): ZIO[WindowsMemoryService, Throwable, Unit] =
    ZIO.serviceWithZIO[WindowsMemoryService](_.moveMemory(dest, src, size))

  def zeroMemory(ptr: Pointer, size: Long): ZIO[WindowsMemoryService, Throwable, Unit] =
    ZIO.serviceWithZIO[WindowsMemoryService](_.zeroMemory(ptr, size))

  def fillMemory(ptr: Pointer, size: Long, fill: Byte): ZIO[WindowsMemoryService, Throwable, Unit] =
    ZIO.serviceWithZIO[WindowsMemoryService](_.fillMemory(ptr, size, fill))

  val live: ULayer[WindowsMemoryService] = ZLayer.succeed(new WindowsMemoryService {
    override def virtualAlloc(size: Long): Task[Pointer] =
      ZioWindowsMemoryJna.virtualAlloc(size)

    override def virtualFree(ptr: Pointer, size: Long): Task[Boolean] =
      ZioWindowsMemoryJna.virtualFree(ptr, size)

    override def moveMemory(dest: Pointer, src: Pointer, size: Long): Task[Unit] =
      ZioWindowsMemoryJna.moveMemory(dest, src, size)

    override def zeroMemory(ptr: Pointer, size: Long): Task[Unit] =
      ZioWindowsMemoryJna.zeroMemory(ptr, size)

    override def fillMemory(ptr: Pointer, size: Long, fill: Byte): Task[Unit] =
      ZioWindowsMemoryJna.fillMemory(ptr, size, fill)
  })
}