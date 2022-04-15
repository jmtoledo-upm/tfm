package TFM

/*
 *
 * An ALU is a minimal start for a processor.
 *
 */

import chisel3._
import chisel3.util._

/**
 * @param sizeOperation:    Int::      Define the number of operation that can be performed by the ALU
 * @param sizeInput:        Int::      Define the length of the inputs of the ALU
 * @param mQueryOperation:  Iterator:: Contains ordered the operations mode
 */
class ALU(sizeOperation: Int = 0, sizeInput: Int = 0) extends Module {
  val mIO = IO(new Bundle {
    val mOperation = Input(UInt(sizeOperation.W))
    val mValA = Input(UInt(sizeInput.W))
    val mValB = Input(UInt(sizeInput.W))
    val mResult = Output(UInt(sizeInput.W))
  })

  // Use shorter variable names
  val mOperation = mIO.mOperation
  val mValA = mIO.mValA
  val mValB = mIO.mValB

  val mResult = Wire(UInt(sizeInput.W))
  // some default value is needed
  mResult := 0.U
  // The ALU selection
  var auxOp = UInt(sizeInput.W)
  auxOp = (mValA >> mValB).asUInt
  auxOp += (1.U(sizeInput.W) << (sizeInput - 1))

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
  // Output on the LEDs
  mIO.mResult := mResult
}


/**
 * ALU Top Level
 *
 * @param sizeOperation:  Int::      Define the number of operation that can be performed by the ALU
 *                                   (will be send to the ALU class)
 * @param sizeInput:      Int::      Define the length of the inputs of the ALU (will be send to the ALU class)
 * @param queryOperation: Iterator:: Contains ordered the operations mode (will be send to the ALU class)
 * @param rangeOperation: Int::      Number of operation INPUT ports
 * @param rangeInput:     Int::      Number of data INPUT ports
 * @param rangeOutput:    Int::      Number of OUTPUT ports
 */
class ALUTopLevel(sizeOperation: Int, sizeInput: Int, rangeOperation: Int, rangeInput: Int,
                  rangeOutput: Int) extends Module {
  val mIOTopLevel = IO(new Bundle {
    val mSizeOperation = sizeOperation
    val mSizeInput = sizeInput
    val mRangeOperation = rangeOperation
    val mRangeInput = rangeInput
    val mSwitch = Input(UInt(((rangeOperation + 1) + (rangeInput + 1)*2).W))
    val mOutput = Output(UInt(rangeOutput.W))
  })

  val mALU = Module(new ALU(mIOTopLevel.mSizeOperation, mIOTopLevel.mSizeInput))

  // Map switches to the ALU input ports (operation and data)
  mALU.mIO.mOperation := mIOTopLevel.mSwitch(mIOTopLevel.mRangeOperation, 0)

  mALU.mIO.mValA := mIOTopLevel.mSwitch(mIOTopLevel.mRangeOperation + mIOTopLevel.mRangeInput,
    mIOTopLevel.mRangeOperation + 1)
  mALU.mIO.mValB := mIOTopLevel.mSwitch(mIOTopLevel.mRangeOperation + mIOTopLevel.mRangeInput*2,
    mIOTopLevel.mRangeOperation + mIOTopLevel.mRangeInput + 1)

  // Map output ports
  mIOTopLevel.mOutput := mALU.mIO.mResult
}

// Generate the Verilog code
object AluMain extends App {
  println("Generating the ALU hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new ALUTopLevel(2,4, 1,
    3, 10), Array("--target-dir", "generated"))

}

