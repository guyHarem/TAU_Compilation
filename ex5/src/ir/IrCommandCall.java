package ir;

import mips.*;
import temp.*;

public class IrCommandCall extends IrCommand {
    public Temp dst;
    public Temp receiver;  // 'this' pointer (only relevant for methods. null for global functions)
    public String label;
    public Temp[] args;

    public IrCommandCall(Temp dst, Temp receiver, String label, Temp[] args) {
        this.dst = dst;
        this.receiver = receiver;
        this.label = label;
        this.args = args;
    }

    private Temp[] insert(Temp val, Temp[] array) {
        Temp[] result = array;
        result = new Temp[array.length + 1];
        result[0] = val;
        System.arraycopy(array, 0, result, 1, array.length);
    }

    @Override
    public void mipsMe() {
        RegAlloc ra = RegAlloc.getInstance();

        List<String> regsToSave = ra.getLiveRegsForCall(this);
        for (String reg : regsToSave) MipsGenerator.getInstance().pushReg(reg);
        MipsGenerator.getInstance().pushReg("$ra");

        Temp[] finalArgs = (receiver != null) ? insert(receiver, args) : args;
        for (int i = 0; i < args.length; i++) {
            String physReg = ra.allocation.get(finalArgs[i]);
            if (i < 4) MipsGenerator.getInstance().moveToReg(String.format("$a%d", i), physReg); // Use $a0 - $a3
            else MipsGenerator.getInstance().pushReg(physReg); // Push remaining args to stack
        }
        MipsGenerator.getInstance().jal(label);

        // Move the result from $v0 to our destination temporary
        if (dst != null) {
            String destPhysReg = ra.allocation.get(dst);
            MipsGenerator.getInstance().moveFromReg("$v0", destPhysReg);
        }
        
        MipsGenerator.getInstance().popReg("$ra");
        for (int i = regsToSave.size() - 1; i >= 0; i--) {
            MipsGenerator.getInstance().popReg(regsToSave.get(i));
        }
    }
}
