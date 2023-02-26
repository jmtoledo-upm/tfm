package TFM

import Chisel.{fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class PETester extends AnyFlatSpec with ChiselScalatestTester {

  "PETester test" should "pass" in {
    test(new PE(4, 4, 4)) { dut =>
      var a = 1
      for (op <- 0 to 9) {
        var result = 0
        var auxOp = 1
        auxOp = (auxOp >> auxOp)
        auxOp += (1 << (4 - 1))
        result = op match {
          case 0 => a + a
          case 1 => a * a
          case 2 => a - a
          case 3 => a << a
          case 4 => auxOp
          case 5 => a >> a
          case 6 => a & a
          case 7 => a | a
          case 8 => a ^ a
          case 9 => a / a
        }
        dut.mIO.mNorthInput.poke(a.U(4.W))
        dut.mIO.mEastInput.poke(a.U(4.W))
        dut.mIO.mWestInput.poke(a.U(4.W))
        dut.mIO.mSouthInput.poke(a.U(4.W))

        dut.mIO.mOperation.poke(op.U(4.W))


        for (mux <- 0 to 3) {
          dut.mIO.mLeftMuxInput.poke(mux.U(4.W))
          dut.mIO.mRightMuxInput.poke(mux.U(4.W))

          for (outmux <- 0 to 4) {
            dut.mIO.mMuxOutput.poke(outmux.U(4.W))

            dut.clock.step()
            outmux match {
              case 0 => dut.mIO.mNorthOutput.expect(a.U(4.W))
              case 1 => dut.mIO.mNorthOutput.expect(a.U(4.W))
              case 2 => dut.mIO.mNorthOutput.expect(a.U(4.W))
              case 3 => dut.mIO.mNorthOutput.expect(a.U(4.W))
              case 4 => dut.mIO.mNorthOutput.expect(result.U(4.W))
            }
          }
        }
      }
    }
  }
}