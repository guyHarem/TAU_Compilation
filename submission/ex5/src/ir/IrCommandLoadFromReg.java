package ir;

import java.util.*;
import mips.*;
import temp.*;

public class IrCommandLoadFromReg extends IrCommand {
    public Temp dst;
    public String srcReg;
    public int offset;

    public IrCommandLoadFromReg(Temp dst, String srcReg, int offset) {
        this.dst = dst;
        this.srcReg = srcReg;
        this.offset = offset;
    }

    @Override
    public List<Temp> getUsedTemps() {
        return Collections.emptyList();
    }

    @Override
    public List<Temp> getDefTemps() {
        return Arrays.asList(dst);
    }

    @Override
    public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().loadFromReg(d, offset, srcReg);
    }
}
