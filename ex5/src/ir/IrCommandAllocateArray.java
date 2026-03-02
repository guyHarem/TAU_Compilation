package ir;
import mips.*;
import temp.*;

public class IrCommandAllocateArray extends IrCommand {
    public Temp dst;
    public Temp size;

    public IrCommandAllocateArray(Temp dst, Temp size) {
        this.dst = dst;
        this.size = size;
    }

    @Override
    public void mipsMe() {
        // MipsGenerator should handle size check (>0) and (size+1)*4 allocation
        MipsGenerator.getInstance().allocateArray(dst, size);
    }
}
