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

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(base, index, src); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String b = RegAlloc.getInstance().allocation.get(base);
        String i = RegAlloc.getInstance().allocation.get(index);
        String s = RegAlloc.getInstance().allocation.get(src);
        MipsGenerator.getInstance().storeArray(b, i, s);
    }
}
