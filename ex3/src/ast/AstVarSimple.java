package ast;

public class AstVarSimple extends AstVar {
    public String name;

    public AstVarSimple(String name, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
    }

	@Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("var (%s)", name));
    }
}
