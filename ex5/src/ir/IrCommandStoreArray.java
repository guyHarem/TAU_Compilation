package ir;

import mips.*;
import temp.*;

public class IrCommandStoreArray extends IrCommand {
    public Temp base;
    public Temp index;
    public Temp src;

    public IrCommandStoreArray(Temp base, Temp index, Temp src) {
        this.base = base;
        this.index = index;
        this.src = src;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().storeArray(base, index, src);
    }
}
