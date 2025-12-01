package ast;

public class AstExpString extends AstExp {
    public String s;

    public AstExpString(String s, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.s = s;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("string (%s)", s));
    }
}
