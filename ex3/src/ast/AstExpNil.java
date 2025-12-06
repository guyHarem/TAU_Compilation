package ast;

public class AstExpNil extends AstExp {
    public AstExpNil(int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, "nil");
    }
}
