package ast;

public class AstNewExpArray extends AstNewExp {
    public AstExp exp;

    public AstNewExpArray(AstType type, AstExp exp, int lineNum) {
        super(type, lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "new array");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
