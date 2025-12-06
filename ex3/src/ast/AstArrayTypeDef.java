package ast;

public class AstArrayTypeDef extends AstDec {
    public String name;
    public AstType type;

    public AstArrayTypeDef(String name, AstType type, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.type = type;
    }

    @Override
    public void printMe() {
        String output = String.format("def array (%s)", name);
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, output);
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }
}
