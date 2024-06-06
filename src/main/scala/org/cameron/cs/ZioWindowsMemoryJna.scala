package org.cameron.cs

import com.sun.jna.Pointer
import zio.{Task, ZIO}

object ZioWindowsMemoryJna {

  def virtualAlloc(size: Long): Task[Pointer] =
    ZIO.attempt {
      val ptr = Kernel32.INSTANCE.VirtualAlloc(null, size, 0x1000 | 0x2000, 0x04)
      if (ptr == null)
        throw new OutOfMemoryError(s"Cannot allocate $size bytes")
      ptr
    }

  def virtualFree(ptr: Pointer, size: Long): Task[Boolean] =
    ZIO.attempt {
      Kernel32.INSTANCE.VirtualFree(ptr, size, 0x8000)
    }

  def moveMemory(dest: Pointer, src: Pointer, size: Long): Task[Unit] =
    ZIO.attempt {
      Kernel32.INSTANCE.RtlMoveMemory(dest, src, size)
    }

  def zeroMemory(ptr: Pointer, size: Long): Task[Unit] =
    ZIO.attempt {
      Kernel32.INSTANCE.RtlZeroMemory(ptr, size)
    }

  def fillMemory(ptr: Pointer, size: Long, fill: Byte): Task[Unit] =
    ZIO.attempt {
      Kernel32.INSTANCE.RtlFillMemory(ptr, size, fill)
    }
}
