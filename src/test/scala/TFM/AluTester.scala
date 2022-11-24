package TFM

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Unitary test for ALU design
 * It will test all of the different operations of the ALU
 */

class AluTester extends AnyFlatSpec with ChiselScalatestTester {

  "AluTester test" should "pass" in {
    test(new ALU(4, 4)) { dut =>
      var b = 1
      var a = 1
      for (op <- 0 to 9) {
            var result = 0
            var auxOp = (a >> b)
            auxOp += (1 << (4 - 1))
            result = op match {
                case 0 => a + b
                case 1 => a * b
                case 2 => a - b
                case 3 => a << b
                case 4 => auxOp
                case 5 => a >> b
                case 6 => a & b
                case 7 => a | b
                case 8 => a ^ b
                case 9 => a / b
              }

            // Operation input
            dut.mIO.mOperation.poke(op.U(4.W))

            // Values inputs
            dut.mIO.mValB.poke(b.U(4.W))
            dut.mIO.mValA.poke(a.U(4.W))

            // Expected result for every operation
            dut.mIO.mResult.expect(result.U(4.W))

          }
        }

    }
}