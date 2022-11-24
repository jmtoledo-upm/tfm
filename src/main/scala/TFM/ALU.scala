package TFM

import chisel3._
import chisel3.util._

/**
 * @param sizeOperation:    Int::      Define the number of operation that can be performed by the ALU
 * @param sizeInput:        Int::      Define the length of the inputs of the ALU
 *
 * Bundle parameters:
 *    mOperation:           Input::    Stores the operation that should be done
 *    mValA:                Input::    Stores one value
 *    mValB:                Input::    Stores a second value
 *    mResult:              Output::   Stores the output of the ALU
 */
class ALU(sizeOperation: Int = 5, sizeInput: Int = 5) extends Module {
  val mIO = IO(new Bundle {
    val mOperation = Input(UInt(sizeOperation.W))
    val mValA = Input(UInt(sizeInput.W))
    val mValB = Input(UInt(sizeInput.W))
    val mResult = Output(UInt(sizeInput.W))
  })

  val mOperation = mIO.mOperation
  val mValA = mIO.mValA
  val mValB = mIO.mValB

  // Create mResult variable as a wire and initialize it to a default value
  val mResult = Wire(UInt(sizeInput.W))
  mResult := 0.U

  // Logic needed for ??
  var auxOp = UInt(sizeInput.W)
  auxOp = (mValA >> mValB).asUInt
  auxOp += (1.U(sizeInput.W) << (sizeInput - 1))

  // Switch between the different operation types
  switch(mOperation) {
    is(0.U) { mResult := mValA + mValB }
    is(1.U) { mResult := mValA * mValB }
    is(2.U) { mResult := mValA - mValB }
    is(3.U) { mResult := mValA << mValB }
    is(4.U) { mResult := auxOp.asUInt }
    is(5.U) { mResult := mValA >> mValB }
    is(6.U) { mResult := mValA & mValB }
    is(7.U) { mResult := mValA | mValB }
    is(8.U) { mResult := mValA ^ mValB }
    is(9.U) { mResult := mValA / mValB }

  }
  // Set as a HW connection the output of the ALU and the wire use to
  // save the value
  mIO.mResult := mResult
}


// Generate the Verilog code
object AluMain extends App {
  println("Generating the ALU hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU(3, 10),
    Array("--target-dir", "generated"))

}

