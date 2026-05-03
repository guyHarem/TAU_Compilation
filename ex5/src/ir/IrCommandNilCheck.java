package ir;

import mips.*;
import temp.*;

public class IrCommandNilCheck extends IrCommand {
    public Temp base;

    public IrCommandNilCheck(Temp base) {
        this.base = base;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().nilCheck(base);
    }
}
