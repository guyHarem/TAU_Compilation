package ast;

public class AstVarSubscript extends AstVar {
    public AstVar var;
    public AstExp subscript;

    public AstVarSubscript(AstVar var, AstExp subscript, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.subscript = subscript;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        if (subscript != null) subscript.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "subscript var");
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
    }
}
