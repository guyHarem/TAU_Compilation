package ast;

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
}
