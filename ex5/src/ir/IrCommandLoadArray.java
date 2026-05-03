package ir;

import mips.*;
import temp.*;

public class IrCommandLoadArray extends IrCommand {
    public Temp dst;
    public Temp base;
    public Temp index;

    public IrCommandLoadArray(Temp dst, Temp base, Temp index) {
        this.dst = dst;
        this.base = base;
        this.index = index;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().loadArray(dst, base, index);
    }
}
