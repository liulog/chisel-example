package example

import chisel3._

/** A small hierarchy: add an external operand to the current counter value. */
class ExampleTop(val width: Int = 8) extends Module {
  require(width > 0, "ExampleTop width must be positive")

  val io = IO(new Bundle {
    val enable = Input(Bool())
    val clear = Input(Bool())
    val operand = Input(UInt(width.W))
    val count = Output(UInt(width.W))
    val sum = Output(UInt((width + 1).W))
    val overflow = Output(Bool())
  })

  val counter = Module(new Counter(width))
  val adder = Module(new Adder(width))

  counter.io.enable := io.enable
  counter.io.clear := io.clear
  adder.io.a := counter.io.value
  adder.io.b := io.operand

  io.count := counter.io.value
  io.sum := adder.io.sum
  io.overflow := counter.io.overflow
}
