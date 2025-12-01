package ast;

public class AstStmtAssign extends AstStmt {
    public AstVar var;
    public AstExp exp;

    public AstStmtAssign(AstVar var, AstExp exp, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.exp = exp;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, ":=");
        AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
