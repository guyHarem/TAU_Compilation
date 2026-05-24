package ir;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import mips.*;
import temp.*;

public class IrCommandCall extends IrCommand {
    public Temp dst;
    public Temp receiver;
    public String label;
    public Temp[] args;

    public IrCommandCall(Temp dst, Temp receiver, String label, Temp[] args) {
        this.dst = dst;
        this.receiver = receiver;
        this.label = label;
        this.args = args;
    }

    private Temp[] insert(Temp val, Temp[] array) {
        Temp[] result = new Temp[array.length + 1];
        result[0] = val;
        System.arraycopy(array, 0, result, 1, array.length);
        return result;
    }

    @Override
    public List<Temp> getUsedTemps() {
        List<Temp> used = new ArrayList<>();
        if (receiver != null) {
            used.add(receiver);
        }
        used.addAll(Arrays.asList(args));
        return used;
    }

    @Override
    public List<Temp> getDefTemps() {
        if (dst != null) {
            return Arrays.asList(dst);
        }
        return Collections.emptyList();
    }

    @Override
    public void mipsMe() {
        RegAlloc ra = RegAlloc.getInstance();
        MipsGenerator g = MipsGenerator.getInstance();
        List<String> regsToSave = ra.getLiveRegsForCall(this);

        Temp[] finalArgs = (receiver != null) ? insert(receiver, args) : args;
        int argSpace = (finalArgs.length > 0)
            ? 16 + Math.max(0, finalArgs.length - 4) * 4
            : 0;
        int saveSpace = (regsToSave.size() + 1) * 4; // +1 for $ra
        int total = argSpace + saveSpace;

        if (total > 0) g.addToSp(-total);

        // Saves go above argSpace (so callee's $fp+8..+20 doesn't overlap them).
        int saveBase = argSpace;
        for (int i = 0; i < regsToSave.size(); i++) {
            g.storeAtReg(regsToSave.get(i), saveBase + i * 4, "$sp");
        }
        g.storeAtReg("$ra", saveBase + regsToSave.size() * 4, "$sp");

        // Args 0..3 → $aN, args 4+ → i*4($sp) inside argSpace.
        for (int i = 0; i < finalArgs.length; i++) {
            String physReg = ra.allocation.get(finalArgs[i]);
            if (i < 4) {
                g.moveToReg(String.format("$a%d", i), physReg);
            } else {
                g.storeAtReg(physReg, i * 4, "$sp");
            }
        }

        g.jal(label);

        // Restore $ra and saved regs.
        g.loadFromReg("$ra", saveBase + regsToSave.size() * 4, "$sp");
        for (int i = regsToSave.size() - 1; i >= 0; i--) {
            g.loadFromReg(regsToSave.get(i), saveBase + i * 4, "$sp");
        }

        if (total > 0) g.addToSp(total);

        if (dst != null) {
            String destPhysReg = ra.allocation.get(dst);
            g.moveFromReg("$v0", destPhysReg);
        }
    }
}
