package ir;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// Store a temp into a local stack slot.
public class IrCommandStoreLocal extends IrCommand {
    public Temp src;
    public int slotIndex;

    public IrCommandStoreLocal(Temp src, int slotIndex) {
        this.src = src;
        this.slotIndex = slotIndex;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(src); }
    @Override public List<Temp> getDefTemps()  { return Collections.emptyList(); }

    @Override
    public void mipsMe() {
        String s = RegAlloc.getInstance().allocation.get(src);
        // slotIndex is a signed offset from $fp.
        MipsGenerator.getInstance().storeAtReg(s, slotIndex, "$fp");
    }
}
