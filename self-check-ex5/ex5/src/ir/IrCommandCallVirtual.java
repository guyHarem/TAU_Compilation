package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mips.*;
import temp.*;

// Virtual method call via vtable: load *recv, load slot, jalr.
// Same caller-save dance as IrCommandCall. $a0 = receiver, $a1.. = args.
public class IrCommandCallVirtual extends IrCommand {
    public Temp dst;
    public Temp receiver;
    public int slot;
    public Temp[] args;

    public IrCommandCallVirtual(Temp dst, Temp receiver, int slot, Temp[] args) {
        this.dst = dst;
        this.receiver = receiver;
        this.slot = slot;
        this.args = args;
    }

    @Override
    public List<Temp> getUsedTemps() {
        List<Temp> used = new ArrayList<>();
        used.add(receiver);
        used.addAll(Arrays.asList(args));
        return used;
    }

    @Override
    public List<Temp> getDefTemps() {
        if (dst != null) return Arrays.asList(dst);
        return Collections.emptyList();
    }

    @Override
    public void mipsMe() {
        RegAlloc ra = RegAlloc.getInstance();
        MipsGenerator g = MipsGenerator.getInstance();
        List<String> regsToSave = ra.getLiveRegsForCall(this);

        Temp[] all = new Temp[args.length + 1];
        all[0] = receiver;
        System.arraycopy(args, 0, all, 1, args.length);

        int argSpace = 16 + Math.max(0, all.length - 4) * 4;
        int saveSpace = (regsToSave.size() + 1) * 4;
        int total = argSpace + saveSpace;

        if (total > 0) g.addToSp(-total);

        int saveBase = argSpace;
        for (int i = 0; i < regsToSave.size(); i++) {
            g.storeAtReg(regsToSave.get(i), saveBase + i * 4, "$sp");
        }
        g.storeAtReg("$ra", saveBase + regsToSave.size() * 4, "$sp");

        for (int i = 0; i < all.length; i++) {
            String physReg = ra.allocation.get(all[i]);
            if (i < 4) {
                g.moveToReg(String.format("$a%d", i), physReg);
            } else {
                g.storeAtReg(physReg, i * 4, "$sp");
            }
        }

        // Indirect call through vtable slot.
        g.loadFromReg("$s0", 0, "$a0");
        g.loadFromReg("$s0", slot * 4, "$s0");
        g.jalr("$s0");

        g.loadFromReg("$ra", saveBase + regsToSave.size() * 4, "$sp");
        for (int i = regsToSave.size() - 1; i >= 0; i--) {
            g.loadFromReg(regsToSave.get(i), saveBase + i * 4, "$sp");
        }

        if (total > 0) g.addToSp(total);

        if (dst != null) {
            String d = ra.allocation.get(dst);
            g.moveFromReg("$v0", d);
        }
    }
}
