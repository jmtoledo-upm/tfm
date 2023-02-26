package TFM

import Chisel.{fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class PESingleTester extends AnyFlatSpec with ChiselScalatestTester {

  "PESingleTester test" should "pass" in {
    test(new PE(4, 4, 4)) { dut =>
      var a = 1
      var result = 2
      var op = 0
      dut.mIO.mNorthInput.poke(a.U(4.W))
      dut.mIO.mEastInput.poke(a.U(4.W))
      dut.mIO.mWestInput.poke(a.U(4.W))
      dut.mIO.mSouthInput.poke(a.U(4.W))

      dut.mIO.mOperation.poke(op.U(4.W))

      var mux = 1
      dut.mIO.mLeftMuxInput.poke(mux.U(4.W))
      dut.mIO.mRightMuxInput.poke(mux.U(4.W))

      dut.mIO.mMuxOutput.poke(4.U(4.W))
      dut.mIO.in.valid.poke(true.B)

      dut.clock.step()
      dut.mIO.out.valid.expect(true.B)
      dut.mIO.mNorthOutput.expect(result.U(4.W))

    }
  }
}