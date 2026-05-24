package ir;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

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
    public List<Temp> getUsedTemps() {
        return Arrays.asList(base, index);
    }

    @Override
    public List<Temp> getDefTemps() {
        return Arrays.asList(dst);
    }

    @Override
    public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        String b = RegAlloc.getInstance().allocation.get(base);
        String i = RegAlloc.getInstance().allocation.get(index);
        MipsGenerator.getInstance().loadArray(d, b, i);
    }
}
