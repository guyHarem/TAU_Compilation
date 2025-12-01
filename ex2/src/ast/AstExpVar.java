package ast;

public class AstExpVar extends AstExp {
    public AstVar var;

    public AstExpVar(AstVar var, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "var exp");
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }
}
