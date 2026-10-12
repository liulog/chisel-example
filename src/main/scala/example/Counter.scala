package example

import chisel3._

/** Enabled, wrapping counter with synchronous clear (higher priority than enable). */
class Counter(val width: Int = 8) extends Module {
  require(width > 0, "Counter width must be positive")

  val io = IO(new Bundle {
    val enable = Input(Bool())
    val clear = Input(Bool())
    val value = Output(UInt(width.W))
    val overflow = Output(Bool())
  })

  val count = RegInit(0.U(width.W))
  when(io.clear) {
    count := 0.U
  }.elsewhen(io.enable) {
    count := count + 1.U
  }

  io.value := count
  // Combinational pulse: asserted during the cycle just before an enabled wrap.
  io.overflow := io.enable && !io.clear && count.andR
}
