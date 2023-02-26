package TFM

import Chisel.{fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class Template3x3Tester extends AnyFlatSpec with ChiselScalatestTester {

  "TemplateTester test" should "pass" in {
    test(new Template3x3(4)) { dut =>
      dut.mIO.mData1.poke(1.U)
      dut.mIO.mData1Mutex.poke(1.U)
      dut.mIO.mP1RightMux.poke(1.U)
      dut.mIO.mP1LeftMux.poke(1.U)
      dut.mIO.mP1OutMux.poke(4.U)
      dut.mIO.mP1Operation.poke(0.U)
      dut.mIO.in.valid.poke(true.B)
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.mIO.out.ready.expect(true.B)
      dut.mIO.mValB.expect(2.U)
      dut.mIO.mP4RightMux.poke(0.U)
      dut.mIO.mP4LeftMux.poke(0.U)
      dut.mIO.mP4OutMux.poke(4.U)
      dut.mIO.mP4Operation.poke(0.U)
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.clock.step()
      dut.mIO.mValA.expect(4.U(4.W))


    }

  }
}