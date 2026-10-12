package example

import circt.stage.ChiselStage

/** Usage: example.GenerateVerilog [output-directory] */
object GenerateVerilog {
  def main(args: Array[String]): Unit = {
    require(args.length <= 1, "Usage: example.GenerateVerilog [output-directory]")
    val outputDirectory = args.headOption.getOrElse("generated")

    ChiselStage.emitSystemVerilogFile(
      new ExampleTop(width = 8),
      args = Array("--target-dir", outputDirectory),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
    println(s"SystemVerilog generated in: $outputDirectory")
  }
}
