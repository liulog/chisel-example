package example

import chisel3._

/** Combinational unsigned adder; the extra output bit preserves carry. */
class Adder(val width: Int = 8) extends Module {
  require(width > 0, "Adder width must be positive")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val sum = Output(UInt((width + 1).W))
  })

  io.sum := io.a +& io.b
}
