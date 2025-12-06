package ast;

public class AstStmtWhile extends AstStmt {
    public AstExp cond;
    public AstList<AstStmt> body;

    public AstStmtWhile(AstExp cond, AstList<AstStmt> body, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;
    }

	@Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (body != null) body.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "while");
        if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }
}