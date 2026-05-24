package ir;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import mips.*;
import temp.*;

public class IrCommandNilCheck extends IrCommand {
    public Temp base;

    public IrCommandNilCheck(Temp base) {
        this.base = base;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(base); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String b = RegAlloc.getInstance().allocation.get(base);
        MipsGenerator.getInstance().nilCheck(b);
    }
}
