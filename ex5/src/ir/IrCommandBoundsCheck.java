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
    public List<Temp> getUsedTemps() {
        return Arrays.asList(base, index);
    }

    @Override
    public List<Temp> getDefTemps() {
        return Collections.emptyList();
    }

    @Override
    public void mipsMe() {
        String b = RegAlloc.getInstance().allocation.get(base);
        String i = RegAlloc.getInstance().allocation.get(index);
        MipsGenerator.getInstance().arrayBoundsCheck(b, i);
    }
}
