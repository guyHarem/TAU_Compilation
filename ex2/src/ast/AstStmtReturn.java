package ast;

public class AstStmtReturn extends AstStmt {
    public AstExp exp;

    public AstStmtReturn(AstExp exp, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "return");
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
