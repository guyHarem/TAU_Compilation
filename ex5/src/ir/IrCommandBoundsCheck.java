package ir;

import mips.*;
import temp.*;

public class IrCommandBoundsCheck extends IrCommand {
    public Temp base;
    public Temp index;

    public IrCommandBoundsCheck(Temp base, Temp index) {
        this.base = base;
        this.index = index;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().arrayBoundsCheck(base, index);
    }
}
