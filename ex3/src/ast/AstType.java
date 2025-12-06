package ast;

public class AstType extends AstNode {
    public String name;

    public AstType(String name, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("type (%s)", name));
    }
}
