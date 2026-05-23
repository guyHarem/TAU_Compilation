package ir;

import java.util.Arrays;
import java.util.List;

import mips.*;
import temp.*;

// Saturation clamp to L's signed range [-32768, 32767].
// Emitted by AstExpBinop after each user-level + - * / op.
public class IrCommandClamp extends IrCommand {
    public Temp t;

    public IrCommandClamp(Temp t) { this.t = t; }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(t); }
    @Override public List<Temp> getDefTemps()  { return Arrays.asList(t); }

    @Override
    public void mipsMe() {
        String r = RegAlloc.getInstance().allocation.get(t);
        MipsGenerator.getInstance().clamp(r);
    }
}
