package ir;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// `new ClassName`: malloc -> zero-fill (in malloc) -> write vtable ptr at offset 0.
public class IrCommandNewObject extends IrCommand {
    public Temp dst;
    public String className;
    public int instanceSize;

    public IrCommandNewObject(Temp dst, String className, int instanceSize) {
        this.dst = dst;
        this.className = className;
        this.instanceSize = instanceSize;
    }

    @Override public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    @Override public List<Temp> getDefTemps()  { return Arrays.asList(dst); }

    @Override
    public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().newObject(d, className, instanceSize);
    }
}
