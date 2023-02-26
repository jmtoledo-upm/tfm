package TFM

import Chisel.{Decoupled, Mux, RegInit, RegNext, SInt, Valid, fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral, switch}
import chisel3.util.is
import chisel3.{Bundle, Flipped, Input, Module, Output, Reg, UInt, Wire, WireInit, when}
import logger.LogLevel.Debug

/**
 * @param sizeOperation:    Int::      Define the number of operation that can be performed by the ALU
 * @param sizeInput:        Int::      Define the length of the inputs of the ALU
 *
 * Bundle parameters:
 *    mNorthInput:          Input::       North value input
 *    mEastInput:           Input::       East value input
 *    mWestInput:           Input::       West value input
 *    mSouthInput:          Input::       South value input


 *    mLeftMuxInput:        Input::       Switch value for the left mux
 *    mRightMuxInput:       Input::       Switch value for the right mux
 *    mMuxOutput:           Input::       Switch value for the output mux
 *
 *    mNorthOutput:         Output::      North value output
 *    mEastOutput:          Output::      East value output
 *    mWestOutput:          Output::      West value output
 *    mSouthOutput:         Output::      South value output
 *
 *    mOperation:           Input::       Operation that should be perform in the ALU
 *
 *    mReady:               Decoupled::   Producer interface for ready/valid communication
 *    mValid:               Flipped: :    Consumer interface for ready/valid communication
 */

class PE(sizeSelection: Int = 4, sizeInput: Int = 4, sizeOperation: Int = 4) extends Module{
  val mIO = IO(new Bundle {

    val mNorthInput    = Input(UInt(sizeInput.W))
    val mEastInput     = Input(UInt(sizeInput.W))
    val mWestInput     = Input(UInt(sizeInput.W))
    val mSouthInput    = Input(UInt(sizeInput.W))

    val mLeftMuxInput  = Input(UInt(4.W))
    val mRightMuxInput = Input(UInt(4.W))
    val mMuxOutput     = Input(UInt(5.W))

    val mNorthOutput   = Output(UInt(sizeInput.W))
    val mEastOutput    = Output(UInt(sizeInput.W))
    val mWestOutput    = Output(UInt(sizeInput.W))
    val mSouthOutput   = Output(UInt(sizeInput.W))

    val mOperation = Input(UInt(sizeOperation.W))

    val in = Flipped(Decoupled(UInt(32.W)))
    val out = Decoupled(UInt(32.W))
  })


  // input and output data registers
  val inReg = RegInit(0.U(32.W))
  val outReg = RegInit(0.U(32.W))

  // input and output valid registers
  val inValidReg = RegInit(false.B)
  val outValidReg = RegNext(false.B)

  // input and output ready registers
  val inReadyReg = RegInit(false.B)
  val outReadyReg = RegInit(false.B)

  // connect input data and valid signals to registers
  when(mIO.in.fire()) {
    inReg := mIO.in.bits
    inValidReg := true.B
  }
  when(mIO.out.fire()) {
    outValidReg := false.B
  }

  // connect output data and valid signals to registers
  mIO.out.bits := outReg
  mIO.out.valid := outValidReg

  // input ready signal logic
  mIO.in.ready := !inValidReg || inReadyReg
  when(mIO.in.fire()) {
    inReadyReg := false.B
  }
  when(!mIO.in.fire() && !inValidReg) {
    inReadyReg := true.B
  }

  // output ready signal logic
  outReadyReg := true.B
  when(mIO.out.fire()) {
    outReadyReg := false.B
  }
  when(!mIO.out.fire() && !outValidReg) {
    outReadyReg := true.B
  }

    val mValA = Wire(UInt(sizeInput.W))
    mValA := 0.U

    val mValB = Wire(UInt(sizeInput.W))
    mValB := 0.U

    mIO.mNorthOutput := 0.U
    mIO.mEastOutput := 0.U
    mIO.mWestOutput := 0.U
    mIO.mSouthOutput := 0.U

    val mOperation = Wire(UInt(sizeOperation.W))
    mOperation := mIO.mOperation

  when(mIO.in.valid) {
    // Initialization of the ready/valid communication
    // Right now the ALU is ready to perform and operation (ready = true),
    // but it doesn't have a correct result (valid = false)
    //mIO.mValid.valid := true.B
    // mIO.mReady.ready := false.B

    // There is not going to be communication between the producer and the
    // consumer using the bits interface, but it should be initialize, if not
    // the compilation will fail
    //mIO.mReady.bits := 0.U

    // mValA will store the value of the left mux selection. By default it is 0
    switch(mIO.mLeftMuxInput) {
      is(0.U) {
        mValA := mIO.mNorthInput
      }
      is(1.U) {
        mValA := mIO.mEastInput
      }
      is(2.U) {
        mValA := mIO.mWestInput
      }
      is(3.U) {
        mValA := mIO.mSouthInput
      }
    }

    // mValB will store the value of the rigth mux selection. By default it is 0

    switch(mIO.mRightMuxInput) {
      is(0.U) {
        mValB := mIO.mNorthInput
      }
      is(1.U) {
        mValB := mIO.mEastInput
      }
      is(2.U) {
        mValB := mIO.mWestInput
      }
      is(3.U) {
        mValB := mIO.mSouthInput
      }
    }

    // Creation and initialization of the ALU module
    val ALU = Module(new ALU(sizeInput, sizeOperation))
    ALU.mIO.mValA := mValA
    ALU.mIO.mValB := mValB
    ALU.mIO.mOperation := mIO.mOperation

    // The result of the output will depend on the output mux value, it can be
    // one of the inputs or the result of the ALU
    val mResultOut = Wire(UInt(sizeInput.W))
    mResultOut := 0.U
    switch(mIO.mMuxOutput) {
      is(0.U) {
        mResultOut := mIO.mNorthInput
      }
      is(1.U) {
        mResultOut := mIO.mEastInput
      }
      is(2.U) {
        mResultOut := mIO.mWestInput
      }
      is(3.U) {
        mResultOut := mIO.mSouthInput
      }
      is(4.U) {
        mResultOut := ALU.mIO.mResult
      }
    }

    //The output value of the mux is stored ina Register
    val auxReg = Reg(UInt(sizeOperation.W))
    auxReg := mResultOut



    // Connect by HW the result with the outputs
    mIO.mNorthOutput := auxReg
    mIO.mEastOutput := auxReg
    mIO.mWestOutput := auxReg
    mIO.mSouthOutput := auxReg

    outValidReg := true.B
  }
}

// Generate the Verilog code
object PEMain extends App {
  println("Generating the PE hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new PE(2,4), Array("--target-dir", "generated"))

}
