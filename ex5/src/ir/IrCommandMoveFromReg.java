package ir;

import mips.*;
import temp.*;

public class IrCommandMoveFromReg extends IrCommand {
    public String reg;
    public Temp dst;

    public IrCommandMoveFromReg(String reg, Temp dst) {
        this.reg = reg;
        this.dst = dst;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().moveFromReg(this.reg, this.dst);
    }
}
