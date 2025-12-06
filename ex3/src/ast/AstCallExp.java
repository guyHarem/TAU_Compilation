package ast;

public class AstCallExp extends AstExp {
    public AstVar var;
    public String funcName;
    public AstList<AstExp> args;

    public AstCallExp(AstVar var, String funcName, AstList<AstExp> args, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.funcName = funcName;
        this.args = args;
    }

    @Override
    public void printMe() {
        if (var != null) var.printMe();
        if (args != null) args.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("call (%s)", funcName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (args != null) AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }
}
