package ast;

public class AstExpInt extends AstExp {
    public int value;

    public AstExpInt(int value, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.value = value;
    }

	@Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("int (%d)", value));
    }
}
