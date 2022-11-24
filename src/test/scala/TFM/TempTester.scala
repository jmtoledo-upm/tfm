package TFM

import Chisel.{fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class TempTester extends AnyFlatSpec with ChiselScalatestTester {

  "TemplateTester test" should "pass" in {
    test(new Template3x3()) { dut =>

      dut.mIO.mP1RightMux.poke(1.U)
      dut.mIO.mP1LeftMux.poke(1.U)
      dut.mIO.mP1OutMux.poke(1.U)
      dut.mIO.mValA.expect(1.U(4.W))


    }

  }
}