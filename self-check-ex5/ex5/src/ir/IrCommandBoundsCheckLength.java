package ir;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// Array allocation length must be > 0; otherwise Access Violation.
public class IrCommandBoundsCheckLength extends IrCommand {
    public Temp len;

    public IrCommandBoundsCheckLength(Temp len) {
        this.len = len;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(len); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String l = RegAlloc.getInstance().allocation.get(len);
        MipsGenerator.getInstance().bleAccessViolation(l);
    }
}
