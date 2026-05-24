package ir;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// Load a local from its stack slot.
public class IrCommandLoadLocal extends IrCommand {
    public Temp dst;
    public int slotIndex;

    public IrCommandLoadLocal(Temp dst, int slotIndex) {
        this.dst = dst;
        this.slotIndex = slotIndex;
    }

    @Override public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    @Override public List<Temp> getDefTemps()  { return Arrays.asList(dst); }

    @Override
    public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        // slotIndex is a signed offset from $fp (negative for body locals,
        // positive for params).
        MipsGenerator.getInstance().loadFromReg(d, slotIndex, "$fp");
    }
}
