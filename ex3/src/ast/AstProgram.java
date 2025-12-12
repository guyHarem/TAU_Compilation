package ast;

import types.*;

public class AstProgram extends AstNode {
    public AstList<AstDec> decList;

    public AstProgram(AstList<AstDec> decList, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.decList = decList;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (decList != null) decList.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "Program");
        if (decList != null) AstGraphviz.getInstance().logEdge(serialNumber, decList.serialNumber);
    }

    @Override
    public Type semantMe() {
        if (decList != null) {
            for (AstList<AstDec> it = decList; it != null; it = it.tail) {
                if (it.head != null) {
                    it.head.semantMe();
                }
            }
        }
        return null;
    }
}
