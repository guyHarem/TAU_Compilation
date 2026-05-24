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
        List<String> regsToSave = ra.getLiveRegsForCall(this);
        for (String reg : regsToSave) {
            MipsGenerator.getInstance().pushReg(reg);
        }
        MipsGenerator.getInstance().pushReg("$ra");

        Temp[] finalArgs = (receiver != null) ? insert(receiver, args) : args;
        int argsMemSize  = (finalArgs.length - 4) * 4;
        for (int i = 0; i < finalArgs.length; i++) {
            String physReg = ra.allocation.get(finalArgs[i]);
            if (i < 4) MipsGenerator.getInstance().moveToReg(String.format("$a%d", i), physReg);
            else MipsGenerator.getInstance().pushReg(physReg);
        }

        MipsGenerator.getInstance().jal(label);

        // These have to be the first operations after return, because they ignore register allocation.
        MipsGenerator.getInstance().popReg("$ra");
        for (int i = regsToSave.size() - 1; i >= 0; i--) {
            MipsGenerator.getInstance().popReg(regsToSave.get(i));
        }

        if (dst != null) {
            String destPhysReg = ra.allocation.get(dst);
            MipsGenerator.getInstance().moveFromReg("$v0", destPhysReg);
        }
        if (argsMemSize > 0 /* If we actually used mem for args */) MipsGenerator.getInstance().addToSp(argsMemSize);
    }
}
