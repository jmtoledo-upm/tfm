package TFM

import Chisel.{Decoupled, Mux, RegInit, fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral, switch}
import chisel3.util.is
import chisel3.{Bundle, Flipped, Input, Module, Output, Reg, UInt, Wire}

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

class PE(sizeSelection: Int = 4, sizeInput: Int = 2, sizeOperation: Int = 2) extends Module{
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

    val mReady = Decoupled(UInt(32.W))
    val mValid = Flipped(Decoupled(UInt(32.W)))
  })

  //Initialization of the variables with the inputs
  val mNorthInput = mIO.mNorthInput
  val mEastInput = mIO.mEastInput
  val mWestInput = mIO.mWestInput
  val mSouthInput = mIO.mSouthInput
  val mLeftMuxInput = mIO.mLeftMuxInput
  val mRightMuxInput = mIO.mRightMuxInput
  val mMuxOutput = mIO.mMuxOutput

  val mOperation = Reg(UInt(sizeOperation.W))
  mOperation := mIO.mOperation

  // Initialization of the ready/valid communication
  // Right now the ALU is ready to perform and operation (ready = true),
  // but it doesn't have a correct result (valid = false)
  mIO.mValid.ready := true.B
  mIO.mReady.valid := false.B

  // There is not going to be communication between the producer and the
  // consumer using the bits interface, but it should be initialize, if not
  // the compilation will fail
  mIO.mReady.bits := 0.U

  // mValA will store the value of the left mux selection. By default it is 0
  val mValA = Wire(UInt(sizeInput.W))
  mValA := 0.U
  switch(mLeftMuxInput) {
    is(0.U) { mValA := mNorthInput }
    is(1.U) { mValA := mEastInput }
    is(2.U) { mValA := mWestInput }
    is(3.U) { mValA := mSouthInput }
  }

  // mValB will store the value of the rigth mux selection. By default it is 0
  val mValB = Wire(UInt(sizeInput.W))
  mValB := 0.U
  switch(mRightMuxInput) {
    is(0.U) { mValB := mNorthInput }
    is(1.U) { mValB := mEastInput }
    is(2.U) { mValB := mWestInput }
    is(3.U) { mValB := mSouthInput }
  }
  // As the ALU starts with the operation, the PE stops being ready (ready = false)
  mIO.mValid.ready := false.B

  // Creation and initialization of the ALU module
  val ALU = Module(new ALU(sizeInput, sizeOperation))
  ALU.mIO.mValA := mValA
  ALU.mIO.mValB := mValB
  ALU.mIO.mOperation := mOperation

  // The results will be store in a register
  val mResult = Reg(UInt(sizeInput.W))
  mResult := ALU.mIO.mResult

  val mRegisterIn = Reg(UInt(sizeInput.W))
  mRegisterIn := mResult
  val mRegisterOut = mRegisterIn

  // The result of the output will depend on the output mux value, it can be
  // one of the inputs or the result of the ALU
  val mResultOut = Wire(UInt(sizeInput.W))
  mResultOut := 0.U
  switch(mMuxOutput) {
    is(0.U) { mResultOut := mNorthInput }
    is(1.U) { mResultOut := mEastInput }
    is(2.U) { mResultOut := mWestInput }
    is(3.U) { mResultOut := mSouthInput }
    is(4.U) { mResultOut := mRegisterOut }
  }

  // Connect by HW the result with the outputs
  mIO.mNorthOutput := mResultOut
  mIO.mEastOutput := mResultOut
  mIO.mWestOutput := mResultOut
  mIO.mSouthOutput := mResultOut

  // Right now the PE has a valid value so it is ready to send it (valid = true)
  mIO.mReady.valid := true.B

}

// Generate the Verilog code
object PEMain extends App {
  println("Generating the PE hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new PE(2,4), Array("--target-dir", "generated"))

}
