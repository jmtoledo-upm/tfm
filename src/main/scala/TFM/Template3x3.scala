package TFM

import Chisel.{Bool, Decoupled, Mux, RegInit, fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral, switch}
import chisel3.util.is
import chisel3.{Bundle, Flipped, Input, Module, Output, Reg, UInt, Wire, when}

class Template3x3(sizeInput: Int = 4) extends Module{
  val mIO = IO(new Bundle {

    val mP1Operation = Input(UInt(sizeInput.W))
    val mP2Operation = Input(UInt(sizeInput.W))
    val mP3Operation = Input(UInt(sizeInput.W))
    val mP4Operation = Input(UInt(sizeInput.W))
    val mP5Operation = Input(UInt(sizeInput.W))
    val mP6Operation = Input(UInt(sizeInput.W))
    val mP7Operation = Input(UInt(sizeInput.W))
    val mP8Operation = Input(UInt(sizeInput.W))
    val mP9Operation = Input(UInt(sizeInput.W))

    val mP1LeftMux = Input(UInt(sizeInput.W))
    val mP1RightMux = Input(UInt(sizeInput.W))
    val mP1OutMux = Input(UInt(sizeInput.W))
    val mP2LeftMux = Input(UInt(sizeInput.W))
    val mP2RightMux = Input(UInt(sizeInput.W))
    val mP2OutMux = Input(UInt(sizeInput.W))
    val mP3LeftMux = Input(UInt(sizeInput.W))
    val mP3RightMux = Input(UInt(sizeInput.W))
    val mP3OutMux = Input(UInt(sizeInput.W))
    val mP4LeftMux = Input(UInt(sizeInput.W))
    val mP4RightMux = Input(UInt(sizeInput.W))
    val mP4OutMux = Input(UInt(sizeInput.W))
    val mP5LeftMux = Input(UInt(sizeInput.W))
    val mP5RightMux = Input(UInt(sizeInput.W))
    val mP5OutMux = Input(UInt(sizeInput.W))
    val mP6LeftMux = Input(UInt(sizeInput.W))
    val mP6RightMux = Input(UInt(sizeInput.W))
    val mP6OutMux = Input(UInt(sizeInput.W))
    val mP7LeftMux = Input(UInt(sizeInput.W))
    val mP7RightMux = Input(UInt(sizeInput.W))
    val mP7OutMux = Input(UInt(sizeInput.W))
    val mP8LeftMux = Input(UInt(sizeInput.W))
    val mP8RightMux = Input(UInt(sizeInput.W))
    val mP8OutMux = Input(UInt(sizeInput.W))
    val mP9LeftMux = Input(UInt(sizeInput.W))
    val mP9RightMux = Input(UInt(sizeInput.W))
    val mP9OutMux = Input(UInt(sizeInput.W))

    val mValA = Output(UInt(sizeInput.W))
    val mValB = Output(UInt(sizeInput.W))

    val mData1Mutex = Input(UInt(sizeInput.W))
    val mData2Mutex = Input(UInt(sizeInput.W))
    val mData3Mutex = Input(UInt(sizeInput.W))

    val mData1 = Input(UInt(sizeInput.W))
    val mData2 = Input(UInt(sizeInput.W))
    val mData3 = Input(UInt(sizeInput.W))

    val in1 = Flipped(Decoupled(UInt(32.W)))
    val out1 = Decoupled(UInt(32.W))
    val in2 = Flipped(Decoupled(UInt(32.W)))
    val out2 = Decoupled(UInt(32.W))
    val in3 = Flipped(Decoupled(UInt(32.W)))
    val out3 = Decoupled(UInt(32.W))
  })

  //Create PEs modules
  val PE1 = Module(new PE())
  val PE2 = Module(new PE())
  val PE3 = Module(new PE())
  val PE4 = Module(new PE())
  val PE5 = Module(new PE())
  val PE6 = Module(new PE())
  val PE7 = Module(new PE())
  val PE8 = Module(new PE())
  val PE9 = Module(new PE())


  //Connect data. The first 3 ones should be able to switch between the input data
  //and the values for the other PEs. The others only receive values from the rest
  //of PEs

  PE1.mIO.mNorthInput := PE7.mIO.mSouthOutput
  PE1.mIO.mEastInput := PE2.mIO.mWestOutput
  PE1.mIO.mSouthInput := PE4.mIO.mNorthOutput
  PE1.mIO.mWestInput := PE3.mIO.mEastOutput

  PE2.mIO.mNorthInput := PE8.mIO.mSouthOutput
  PE2.mIO.mEastInput := PE3.mIO.mWestOutput
  PE2.mIO.mSouthInput := PE5.mIO.mNorthOutput
  PE2.mIO.mWestInput := PE1.mIO.mEastOutput

  PE3.mIO.mNorthInput := PE9.mIO.mSouthOutput
  PE3.mIO.mEastInput := PE1.mIO.mWestOutput
  PE3.mIO.mSouthInput := PE6.mIO.mNorthOutput
  PE3.mIO.mWestInput := PE2.mIO.mEastOutput

  switch(mIO.mData1Mutex) {
    is(0.U) {
      PE1.mIO.mNorthInput := PE7.mIO.mSouthOutput
      PE1.mIO.mEastInput := PE2.mIO.mWestOutput
      PE1.mIO.mSouthInput := PE4.mIO.mNorthOutput
      PE1.mIO.mWestInput := PE3.mIO.mEastOutput
    }
    is(1.U) {
      PE1.mIO.mNorthInput := mIO.mData1
      PE1.mIO.mEastInput := mIO.mData1
      PE1.mIO.mSouthInput := mIO.mData1
      PE1.mIO.mWestInput := mIO.mData1
    }  }

  switch(mIO.mData2Mutex) {
    is(0.U) {
      PE2.mIO.mNorthInput := PE8.mIO.mSouthOutput
      PE2.mIO.mEastInput := PE3.mIO.mWestOutput
      PE2.mIO.mSouthInput := PE5.mIO.mNorthOutput
      PE2.mIO.mWestInput := PE1.mIO.mEastOutput
    }
    is(1.U) {
      PE2.mIO.mNorthInput := mIO.mData2
      PE2.mIO.mEastInput := mIO.mData2
      PE2.mIO.mSouthInput := mIO.mData2
      PE2.mIO.mWestInput := mIO.mData2
    }
  }

  switch(mIO.mData3Mutex) {
    is(0.U) {
      PE3.mIO.mNorthInput := PE9.mIO.mSouthOutput
      PE3.mIO.mEastInput := PE1.mIO.mWestOutput
      PE3.mIO.mSouthInput := PE6.mIO.mNorthOutput
      PE3.mIO.mWestInput := PE2.mIO.mEastOutput
    }
    is(1.U) {
      PE3.mIO.mNorthInput := mIO.mData3
      PE3.mIO.mEastInput := mIO.mData3
      PE3.mIO.mSouthInput := mIO.mData3
      PE3.mIO.mWestInput := mIO.mData3
    }
  }


  PE4.mIO.mNorthInput := PE1.mIO.mSouthOutput
  PE4.mIO.mEastInput := PE5.mIO.mWestOutput
  PE4.mIO.mSouthInput := PE7.mIO.mNorthOutput
  PE4.mIO.mWestInput := PE6.mIO.mEastOutput

  PE5.mIO.mNorthInput := PE2.mIO.mSouthOutput
  PE5.mIO.mEastInput := PE6.mIO.mWestOutput
  PE5.mIO.mSouthInput := PE8.mIO.mNorthOutput
  PE5.mIO.mWestInput := PE4.mIO.mEastOutput

  PE6.mIO.mNorthInput := PE3.mIO.mSouthOutput
  PE6.mIO.mEastInput := PE4.mIO.mWestOutput
  PE6.mIO.mSouthInput := PE9.mIO.mNorthOutput
  PE6.mIO.mWestInput := PE5.mIO.mEastOutput

  PE7.mIO.mNorthInput := PE4.mIO.mSouthOutput
  PE7.mIO.mEastInput := PE8.mIO.mWestOutput
  PE7.mIO.mSouthInput := PE1.mIO.mNorthOutput
  PE7.mIO.mWestInput := PE9.mIO.mEastOutput

  PE8.mIO.mNorthInput := PE5.mIO.mSouthOutput
  PE8.mIO.mEastInput := PE9.mIO.mWestOutput
  PE8.mIO.mSouthInput := PE2.mIO.mNorthOutput
  PE8.mIO.mWestInput := PE7.mIO.mEastOutput

  PE9.mIO.mNorthInput := PE6.mIO.mSouthOutput
  PE9.mIO.mEastInput := PE7.mIO.mWestOutput
  PE9.mIO.mSouthInput := PE3.mIO.mNorthOutput
  PE9.mIO.mWestInput := PE8.mIO.mEastOutput


  //Create the connection with the operation values of the PEs
  PE1.mIO.mOperation := mIO.mP1Operation
  PE2.mIO.mOperation := mIO.mP2Operation
  PE3.mIO.mOperation := mIO.mP3Operation
  PE4.mIO.mOperation := mIO.mP4Operation
  PE5.mIO.mOperation := mIO.mP5Operation
  PE6.mIO.mOperation := mIO.mP6Operation
  PE7.mIO.mOperation := mIO.mP7Operation
  PE8.mIO.mOperation := mIO.mP8Operation
  PE9.mIO.mOperation := mIO.mP9Operation

  //Connect the different muxes of the PEs
  PE1.mIO.mLeftMuxInput := mIO.mP1LeftMux
  PE1.mIO.mRightMuxInput := mIO.mP1RightMux
  PE1.mIO.mMuxOutput := mIO.mP1OutMux

  PE2.mIO.mLeftMuxInput := mIO.mP2LeftMux
  PE2.mIO.mRightMuxInput := mIO.mP2RightMux
  PE2.mIO.mMuxOutput := mIO.mP2OutMux

  PE3.mIO.mLeftMuxInput := mIO.mP3LeftMux
  PE3.mIO.mRightMuxInput := mIO.mP3RightMux
  PE3.mIO.mMuxOutput := mIO.mP3OutMux

  PE4.mIO.mLeftMuxInput := mIO.mP4LeftMux
  PE4.mIO.mRightMuxInput := mIO.mP4RightMux
  PE4.mIO.mMuxOutput := mIO.mP4OutMux

  PE5.mIO.mLeftMuxInput := mIO.mP5LeftMux
  PE5.mIO.mRightMuxInput := mIO.mP5RightMux
  PE5.mIO.mMuxOutput := mIO.mP5OutMux

  PE6.mIO.mLeftMuxInput := mIO.mP6LeftMux
  PE6.mIO.mRightMuxInput := mIO.mP6RightMux
  PE6.mIO.mMuxOutput := mIO.mP6OutMux

  PE7.mIO.mLeftMuxInput := mIO.mP7LeftMux
  PE7.mIO.mRightMuxInput := mIO.mP7RightMux
  PE7.mIO.mMuxOutput := mIO.mP7OutMux

  PE8.mIO.mLeftMuxInput := mIO.mP8LeftMux
  PE8.mIO.mRightMuxInput := mIO.mP8RightMux
  PE8.mIO.mMuxOutput := mIO.mP8OutMux

  PE9.mIO.mLeftMuxInput := mIO.mP9LeftMux
  PE9.mIO.mRightMuxInput := mIO.mP9RightMux
  PE9.mIO.mMuxOutput := mIO.mP9OutMux


  //Set the valid/ready structure
  PE1.mIO.in <> mIO.in1
  PE4.mIO.in <> PE1.mIO.out
  PE7.mIO.in <> PE4.mIO.out
  mIO.out1 <> PE7.mIO.out

  PE2.mIO.in <> mIO.in2
  PE5.mIO.in <> PE2.mIO.out
  PE8.mIO.in <> PE5.mIO.out
  mIO.out2 <> PE8.mIO.out

  PE3.mIO.in <> mIO.in3
  PE6.mIO.in <> PE3.mIO.out
  PE9.mIO.in <> PE6.mIO.out
  mIO.out3 <> PE9.mIO.out


  mIO.mValA := 0.U
  mIO.mValA := PE4.mIO.mSouthOutput

  mIO.mValB := 0.U
  mIO.mValB := PE7.mIO.mSouthOutput
}
