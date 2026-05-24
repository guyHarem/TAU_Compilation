package ir;

import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// vtable_<class>: .word <m0>, <m1>, ...   in the .data segment.
public class IrCommandVtable extends IrCommand {
    public String className;
    public List<String> methodLabels;

    public IrCommandVtable(String className, List<String> methodLabels) {
        this.className = className;
        this.methodLabels = methodLabels;
    }

    @Override public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    @Override public List<Temp> getDefTemps()  { return Collections.emptyList(); }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().emitVtable(className, methodLabels);
    }
}
