package TFM

import Chisel.{Bool, Decoupled, Mux, RegInit, fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral, switch}
import chisel3.util.is
import chisel3.{Bundle, Flipped, Input, Module, Output, Reg, UInt, Wire, when}

class Template1x2(sizeInput: Int = 4) extends Module{
  val mIO = IO(new Bundle {

    val mP1Operation = Input(UInt(sizeInput.W))
    val mP2Operation = Input(UInt(sizeInput.W))

    val mP1LeftMux = Input(UInt(sizeInput.W))
    val mP1RightMux = Input(UInt(sizeInput.W))
    val mP1OutMux = Input(UInt(sizeInput.W))
    val mP2LeftMux = Input(UInt(sizeInput.W))
    val mP2RightMux = Input(UInt(sizeInput.W))
    val mP2OutMux = Input(UInt(sizeInput.W))

    val mValA = Output(UInt(sizeInput.W))
    val mValB = Output(UInt(sizeInput.W))
    val mValC = Output(UInt(sizeInput.W))
    val mValD = Output(UInt(sizeInput.W))

    val mData1Mutex = Input(UInt(sizeInput.W))
    val mData2Mutex = Input(UInt(sizeInput.W))

    val mData1 = Input(UInt(sizeInput.W))
    val mData2 = Input(UInt(sizeInput.W))

    val in = Flipped(Decoupled(UInt(32.W)))
    val out = Decoupled(UInt(32.W))

  })

  val PE1 = Module(new PE())
  val PE2 = Module(new PE())


  PE1.mIO.mNorthInput := PE1.mIO.mSouthOutput
  PE1.mIO.mEastInput := PE2.mIO.mWestOutput
  PE1.mIO.mSouthInput := PE1.mIO.mNorthOutput
  PE1.mIO.mWestInput := PE2.mIO.mEastOutput

  PE2.mIO.mNorthInput := PE2.mIO.mSouthOutput
  PE2.mIO.mEastInput := PE1.mIO.mWestOutput
  PE2.mIO.mSouthInput := PE2.mIO.mNorthOutput
  PE2.mIO.mWestInput := PE1.mIO.mEastOutput

  switch(mIO.mData1Mutex) {
    is(0.U) {
      PE1.mIO.mNorthInput := PE1.mIO.mSouthOutput
      PE1.mIO.mEastInput := PE2.mIO.mWestOutput
      PE1.mIO.mSouthInput := PE1.mIO.mNorthOutput
      PE1.mIO.mWestInput := PE2.mIO.mEastOutput
    }
    is(1.U) {
      PE1.mIO.mNorthInput := mIO.mData1
      PE1.mIO.mEastInput := mIO.mData1
      PE1.mIO.mSouthInput := mIO.mData1
      PE1.mIO.mWestInput := mIO.mData1
    }
  }

  switch(mIO.mData2Mutex) {
    is(0.U) {
      PE2.mIO.mNorthInput := PE2.mIO.mSouthOutput
      PE2.mIO.mEastInput := PE1.mIO.mWestOutput
      PE2.mIO.mSouthInput := PE2.mIO.mNorthOutput
      PE2.mIO.mWestInput := PE1.mIO.mEastOutput
    }
    is(1.U) {
      PE2.mIO.mNorthInput := mIO.mData2
      PE2.mIO.mEastInput := mIO.mData2
      PE2.mIO.mSouthInput := mIO.mData2
      PE2.mIO.mWestInput := mIO.mData2
    }
  }

  PE1.mIO.mOperation := mIO.mP1Operation
  PE2.mIO.mOperation := mIO.mP2Operation

  PE1.mIO.mLeftMuxInput := mIO.mP1LeftMux
  PE1.mIO.mRightMuxInput := mIO.mP1RightMux
  PE1.mIO.mMuxOutput := mIO.mP1OutMux

  PE2.mIO.mLeftMuxInput := mIO.mP2LeftMux
  PE2.mIO.mRightMuxInput := mIO.mP2RightMux
  PE2.mIO.mMuxOutput := mIO.mP2OutMux

  // Connect input of PE1 to input of MyModule
  PE1.mIO.in <> mIO.in

  // Connect output of PE1 to input of PE2
  PE2.mIO.in <> PE1.mIO.out

  // Connect output of PE2 to output of MyModule
  mIO.out <> PE2.mIO.out

  mIO.mValA := PE1.mIO.mWestOutput
  mIO.mValB := PE2.mIO.mWestOutput
  mIO.mValC := PE1.mIO.mWestOutput
  mIO.mValD := PE2.mIO.mWestOutput


}