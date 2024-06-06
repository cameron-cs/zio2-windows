package org.cameron.cs

import com.sun.jna.{Library, Native, Pointer}
import com.sun.jna.win32.W32APIOptions

trait Kernel32 extends Library {
  def VirtualAlloc(lpAddress: Pointer, dwSize: Long, flAllocationType: Int, flProtect: Int): Pointer
  def VirtualFree(lpAddress: Pointer, dwSize: Long, dwFreeType: Int): Boolean
  def RtlMoveMemory(dest: Pointer, src: Pointer, length: Long): Unit
  def RtlZeroMemory(dest: Pointer, length: Long): Unit
  def RtlFillMemory(dest: Pointer, length: Long, fill: Byte): Unit

  def CreateFile(lpFileName: String, dwDesiredAccess: Int, dwShareMode: Int, lpSecurityAttributes: Pointer, dwCreationDisposition: Int, dwFlagsAndAttributes: Int, hTemplateFile: Pointer): Pointer
  def CloseHandle(hObject: Pointer): Boolean
  def ReadFile(hFile: Pointer, lpBuffer: Pointer, nNumberOfBytesToRead: Int, lpNumberOfBytesRead: Pointer, lpOverlapped: Pointer): Boolean
  def WriteFile(hFile: Pointer, lpBuffer: Pointer, nNumberOfBytesToWrite: Int, lpNumberOfBytesWritten: Pointer, lpOverlapped: Pointer): Boolean
  def SetFilePointer(hFile: Pointer, lDistanceToMove: Int, lpDistanceToMoveHigh: Pointer, dwMoveMethod: Int): Pointer
}

object Kernel32 {
  val INSTANCE: Kernel32 = Native.load("Kernel32", classOf[Kernel32], W32APIOptions.DEFAULT_OPTIONS)
}