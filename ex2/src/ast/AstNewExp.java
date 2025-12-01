package ast;

public class AstNewExp extends AstExp {
    public AstType type;

    public AstNewExp(AstType type, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "new");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }
}
