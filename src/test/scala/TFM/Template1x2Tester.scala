package TFM

import Chisel.{fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.{ChiselScalatestTester, decoupledToDriver}
import org.scalatest.flatspec.AnyFlatSpec

class Template1x2Tester extends AnyFlatSpec with ChiselScalatestTester {

  "TemplateTester test" should "pass" in {
    test(new Template1x2(4)) { dut =>

      dut.mIO.in.valid.poke(false.B)
      dut.mIO.mP1Operation.poke(0.U)
      dut.mIO.mData1.poke(1.U)
      dut.mIO.mData2.poke(1.U)
      dut.mIO.mData1Mutex.poke(1.U)
      dut.mIO.mData2Mutex.poke(0.U)

      dut.mIO.mP1RightMux.poke(0.U)
      dut.mIO.mP1LeftMux.poke(0.U)
      dut.mIO.mP1OutMux.poke(4.U)
      dut.mIO.mP1Operation.poke(1.U)

      dut.mIO.mP2RightMux.poke(2.U)
      dut.mIO.mP2LeftMux.poke(2.U)
      dut.mIO.mP2Operation.poke(0.U)
      dut.mIO.mP2OutMux.poke(4.U)
      dut.mIO.in.valid.poke(true.B)

      dut.clock.step()
      dut.mIO.mValA.expect(1.U)
      dut.clock.step()
      dut.mIO.mValB.expect(2.U)

      dut.mIO.mData1Mutex.poke(0.U)
      dut.mIO.mData2Mutex.poke(0.U)
      dut.mIO.mP1RightMux.poke(1.U)
      dut.mIO.mP1LeftMux.poke(1.U)
      dut.mIO.mP1OutMux.poke(4.U)
      dut.mIO.mP1Operation.poke(0.U)

      dut.clock.step()
      dut.mIO.mValC.expect(4.U)

    }

  }
}