package TFM

import Chisel.{fromBooleanToLiteral, fromIntToWidth, fromtIntToLiteral}
import chisel3.tester.{testableClock, testableData}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class Template3x3Tester extends AnyFlatSpec with ChiselScalatestTester {

  "Template3x3Tester test" should "pass" in {
    test(new Template3x3(4)) { dut =>

      //Columna 1
      dut.mIO.in1.valid.poke(false.B)
      dut.mIO.in2.valid.poke(false.B)
      dut.mIO.in3.valid.poke(false.B)

      dut.mIO.mP1Operation.poke(0.U)
      dut.mIO.mData1.poke(1.U)
      dut.mIO.mData1Mutex.poke(1.U)

      dut.mIO.mP1RightMux.poke(0.U)
      dut.mIO.mP1LeftMux.poke(0.U)
      dut.mIO.mP1OutMux.poke(4.U)
      dut.mIO.mP1Operation.poke(1.U)

      dut.mIO.mP4RightMux.poke(0.U)
      dut.mIO.mP4LeftMux.poke(0.U)
      dut.mIO.mP4Operation.poke(0.U)
      dut.mIO.mP4OutMux.poke(4.U)

      dut.mIO.mP7RightMux.poke(0.U)
      dut.mIO.mP7LeftMux.poke(0.U)
      dut.mIO.mP7Operation.poke(0.U)
      dut.mIO.mP7OutMux.poke(4.U)
      dut.mIO.in1.valid.poke(true.B)

      //Columna 2
      dut.mIO.mP2Operation.poke(0.U)
      dut.mIO.mData2.poke(1.U)
      dut.mIO.mData2Mutex.poke(1.U)

      dut.mIO.mP2RightMux.poke(0.U)
      dut.mIO.mP2LeftMux.poke(0.U)
      dut.mIO.mP2OutMux.poke(4.U)
      dut.mIO.mP2Operation.poke(1.U)

      dut.mIO.mP5RightMux.poke(0.U)
      dut.mIO.mP5LeftMux.poke(0.U)
      dut.mIO.mP5Operation.poke(0.U)
      dut.mIO.mP5OutMux.poke(4.U)

      dut.mIO.mP8RightMux.poke(0.U)
      dut.mIO.mP8LeftMux.poke(0.U)
      dut.mIO.mP8Operation.poke(0.U)
      dut.mIO.mP8OutMux.poke(4.U)
      dut.mIO.in2.valid.poke(true.B)

      //Columna 3
      dut.mIO.mP3Operation.poke(0.U)
      dut.mIO.mData3.poke(1.U)
      dut.mIO.mData3Mutex.poke(1.U)

      dut.mIO.mP3RightMux.poke(0.U)
      dut.mIO.mP3LeftMux.poke(0.U)
      dut.mIO.mP3OutMux.poke(4.U)
      dut.mIO.mP3Operation.poke(1.U)

      dut.mIO.mP6RightMux.poke(0.U)
      dut.mIO.mP6LeftMux.poke(0.U)
      dut.mIO.mP6Operation.poke(0.U)
      dut.mIO.mP6OutMux.poke(4.U)

      dut.mIO.mP9RightMux.poke(0.U)
      dut.mIO.mP9LeftMux.poke(0.U)
      dut.mIO.mP9Operation.poke(0.U)
      dut.mIO.mP9OutMux.poke(4.U)
      dut.mIO.in3.valid.poke(true.B)

      dut.clock.step()
      dut.clock.step()
      dut.mIO.mValA.expect(2.U)
      dut.clock.step()
      dut.mIO.mValB.expect(4.U)


    }

  }
}