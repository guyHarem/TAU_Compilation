package ast;

import ir.*;
import temp.*;
import types.*;

public class AstExpNil extends AstExp {
    public AstExpNil(int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, "nil");
    }

    @Override
    public Type semantMe() {
        return TypeNil.getInstance();
    }

    @Override
    public Temp irMe() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, 0));
        return t;
    }
}
