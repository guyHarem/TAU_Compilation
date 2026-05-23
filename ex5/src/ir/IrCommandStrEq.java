package ir;

import java.util.Arrays;
import java.util.List;

import mips.*;
import temp.*;

// Byte-by-byte string contents-equality. Returns 1 in dst if the two
// null-terminated strings have identical contents, 0 otherwise.
public class IrCommandStrEq extends IrCommand {
    private Temp dst;
    private Temp s1;
    private Temp s2;

    public IrCommandStrEq(Temp dst, Temp s1, Temp s2) {
        this.dst = dst;
        this.s1  = s1;
        this.s2  = s2;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(s1, s2); }
    @Override public List<Temp> getDefTemps()  { return Arrays.asList(dst); }

    @Override
    public void mipsMe() {
        String d  = RegAlloc.getInstance().allocation.get(dst);
        String r1 = RegAlloc.getInstance().allocation.get(s1);
        String r2 = RegAlloc.getInstance().allocation.get(s2);
        MipsGenerator.getInstance().strEq(d, r1, r2);
    }
}
