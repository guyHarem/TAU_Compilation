package ast;

public class AstNewExp extends AstExp {

    public AstType type;
    
    public AstNewExp(AstType type, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
    }

    public void printMe() {
        System.out.format("AST NODE NEW EXPRESSION\n");

        if (type != null) type.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "NEW\nEXP");

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }
}
