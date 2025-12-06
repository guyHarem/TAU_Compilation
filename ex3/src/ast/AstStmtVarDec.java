package ast;

public class AstStmtVarDec extends AstStmt {
    public AstVarDec dec;

    public AstStmtVarDec(AstVarDec var, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.dec = var;
    }

    @Override
    public void printMe() {
        if (dec != null) dec.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "var dec stmt");
        if (dec != null) AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }
}
