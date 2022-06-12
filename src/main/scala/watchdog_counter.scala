
import chisel3._

class wdgCntIO(width: Int) extends Bundle {
  val wdg_en = Input(UInt(1.W))
  val period = Input(UInt(width.W))
  val int_clr = Input(UInt(1.W))
  var timeout_int = Output(UInt(1.W))
  val timeout_rst = Output(UInt(1.W))
}

class wdg_counter (width: Int) extends Module{
  val io = IO(new wdgCntIO(width))
  val count = RegInit(UInt(width.W),0.U)
  val out_rst = RegInit(UInt(1.W),0.U)
  val out_int = RegInit(UInt(1.W),0.U)

  count:=Mux((!io.wdg_en)||(count===io.period)||(io.timeout_rst===1.U),0.U,count+1.U)

  when((!io.wdg_en)||(io.int_clr===1.U)){out_int:=0.U}
  .elsewhen(count===io.period)          {out_int:=1.U}

  out_rst:=Mux((count===io.period)&&(io.timeout_int===1.U),1.U,out_rst)

  io.timeout_int:=out_int
  io.timeout_rst:=out_rst
}

object generator extends App{
  //println(args(0))
  (new chisel3.stage.ChiselStage).emitVerilog(new wdg_counter(32))
}