package ast;

public class AstProgram extends AstNode {
    public AstList<AstDec> decList;

    public AstProgram(AstList<AstDec> decList, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.decList = decList;
    }

    @Override
    public void printMe() {
        if (decList != null) decList.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "Program");
        if (decList != null) AstGraphviz.getInstance().logEdge(serialNumber, decList.serialNumber);
    }
}
