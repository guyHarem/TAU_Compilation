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

    @Override
    public void mipsMe() {
        // Pass arguments to $a0, $a1, $a2, $a3
        // TODO: Change and support >= 4
        for (int i = 0; i < args.length && i < 4; i++) {
            MipsGenerator.getInstance().move(
                String.format("$a%d", i), 
                args[i]
            );
        }

        // TODO: If it's a method call, ensure the receiver is in $a0 or handled
        // (This depends on your specific class/method implementation)

        // Jump and Link to the function label (TODO: Handle method call).
        MipsGenerator.getInstance().jal(label);

        // Move the result from $v0 to our destination temporary
        // Table 1 shows that return values/results typically reside in $v0 
        if (dst != null) {
            MipsGenerator.getInstance().moveFromV0(dst);
        }
    }
}
