package ir;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import mips.*;
import temp.*;

public class IrCommandMoveFromReg extends IrCommand {
    public String reg;
    public Temp dst;

    public IrCommandMoveFromReg(String reg, Temp dst) {
        this.reg = reg;
        this.dst = dst;
    }

    @Override public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    @Override public List<Temp> getDefTemps() { return Arrays.asList(dst); }
    @Override public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().moveFromReg(this.reg, d);
    }
}
