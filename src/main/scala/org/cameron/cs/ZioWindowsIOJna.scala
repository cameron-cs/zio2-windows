package org.cameron.cs

import com.sun.jna.{Memory, Pointer}
import zio.{Task, ZIO}

object ZioWindowsIOJna {
  
  
  def createFile(pathname: String,
                 access: Int,
                 shareMode: Int,
                 creationDisposition: Int,
                 flagsAndAttributes: Int): Task[Pointer] =
    ZIO.attempt {
      val handle = Kernel32.INSTANCE.CreateFile(pathname, access, shareMode, null, creationDisposition, flagsAndAttributes, null)
      if (handle == null)
        throw new RuntimeException(s"Cannot create file: $pathname")
      handle
    }
    
  def closeHandle(handle: Pointer): Task[Boolean] =
    ZIO.attempt {
      Kernel32.INSTANCE.CloseHandle(handle)
    }

  def readFile(handle: Pointer, buffer: Pointer, bytesToRead: Int): Task[Int] =
    ZIO.attempt {
      val bytesRead = new Memory(4)
      if (!Kernel32.INSTANCE.ReadFile(handle, buffer, bytesToRead, bytesRead, null))
        throw new RuntimeException(s"Cannot read from file: $handle")
      else
        bytesRead.getInt(0)
    }


  def writeFile(handle: Pointer, buffer: Pointer, bytesToWrite: Int): Task[Int] =
    ZIO.attempt {
      val bytesWritten = new Memory(4)
      if (!Kernel32.INSTANCE.WriteFile(handle, buffer, bytesToWrite, bytesWritten, null))
        throw new RuntimeException(s"Cannot write to file: $handle")
      else
        bytesWritten.getInt(0)
    }

  def setFilePointer(handle: Pointer, distance: Int, moveMethod: Int): Task[Pointer] =
    ZIO.attempt {
      val result = Kernel32.INSTANCE.SetFilePointer(handle, distance, null, moveMethod)
      if (result == null) 
        throw new RuntimeException(s"Cannot set file pointer: $handle")
      result
    }
}
