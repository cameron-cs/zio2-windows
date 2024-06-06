# zio2-windows
This project provides a ZIO-based wrapper for Windows I/O and memory management functions using the Java Native Access (JNA) library. The project includes functionality for memory allocation, I/O operations, and various system-level tasks, making it easier to interact with low-level system calls in a type-safe and functional way using ZIO.

## Features

- **Memory operations**:
    - `virtualAlloc(size: Long): Pointer`: Allocates memory of given size.
    - `virtualFree(ptr: Pointer, size: Long): Boolean`: Frees allocated memory.
    - `moveMemory(dest: Pointer, src: Pointer, size: Long): Unit`: Copies memory from source to destination.
    - `zeroMemory(ptr: Pointer, size: Long): Unit`: Fills memory with zeros.
    - `fillMemory(ptr: Pointer, size: Long, fill: Byte): Unit`: Fills memory with a specified byte.

- **I/O operations**:
    - `createFile(pathname: String, access: Int, shareMode: Int, creationDisposition: Int, flagsAndAttributes: Int): Pointer`: Creates or opens a file.
    - `closeHandle(handle: Pointer): Boolean`: Closes an open object handle.
    - `readFile(handle: Pointer, buffer: Pointer, bytesToRead: Int): Int`: Reads data from a file.
    - `writeFile(handle: Pointer, buffer: Pointer, bytesToWrite: Int): Int`: Writes data to a file.
    - `setFilePointer(handle: Pointer, distance: Int, moveMethod: Int): Pointer`: Moves the file pointer to a specified location.

## Prerequisites

- Java 8 or higher.
- SBT or Maven for building the project.
- Windows operating system for running the native I/O and memory operations.
- JNA library.

## Project structure

- **WinLib.scala** defines the JNA interface to the Windows API functions.

- **WindowsIOService.scala** provides ZIO-based services for I/O operations.

- **WindowsMemoryService.scala** provides ZIO-based services for memory operations.

## Usage example
Here is an example of how to use the Windows I/O and Memory services in your ZIO application:

```scala
import org.cameron.cs.{WindowsIOService, WindowsMemoryService}
import zio._
import com.sun.jna.{Memory, Pointer}

object WindowsExampleApp extends ZIOAppDefault {

  def run = for {
    // create a file
    handle <- WindowsIOService.createFile("C:\\temp\\testfile", 0x40000000 | 0x80000000, 0, 1, 0).provideLayer(WindowsIOService.live)

    // allocate memory
    source <- WindowsMemoryService.virtualAlloc(8).provideLayer(WindowsMemoryService.live)
    _ = source.setLong(0, 12345678L)
    
    // write to file
    _ <- WindowsIOService.writeFile(handle, source, 8).provideLayer(WindowsIOService.live)
    
    // set file pointer to the start of the file
    _ <- WindowsIOService.setFilePointer(handle, 0, 0).provideLayer(WindowsIOService.live)
    
    // read from file
    readBuffer <- ZIO.succeed(new Memory(8))
    _ <- WindowsIOService.readFile(handle, readBuffer, 8).provideLayer(WindowsIOService.live)
    readValue = readBuffer.getLong(0)
    
    // print read value
    _ <- Console.printLine(s"Read value: $readValue")
    
    // close the file
    _ <- WindowsIOService.closeHandle(handle).provideLayer(WindowsIOService.live)

  } yield ()
}
