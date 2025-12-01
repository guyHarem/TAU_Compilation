package ast;

public class AstStmtCall extends AstStmt {
    public AstCallExp exp;

    public AstStmtCall(AstCallExp exp, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "call stmt");
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
