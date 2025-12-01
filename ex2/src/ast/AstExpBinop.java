package ast;

public class AstExpBinop extends AstExp {
    int op;
    public AstExp left;
    public AstExp right;

    public AstExpBinop(AstExp left, AstExp right, int op, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.left = left;
        this.right = right;
        this.op = op;
    }

	@Override
    public void printMe() {
		String sop;
        switch (op) {
            case 0:  sop = "+";  break;
            case 1:  sop = "-";  break;
            case 2:  sop = "*";  break;
            case 3:  sop = "/";  break;
            case 4:  sop = "<";  break;
            case 5:  sop = ">";  break;
            case 6:  sop = "=="; break;
            default: sop = "?";  break;
        }

        if (left != null) left.printMe();
        if (right != null) right.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, sop);
        if (left != null) AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);
        if (right != null) AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
    }
}
