package ast;

public class AstNewExpArray extends AstNewExp {

    public AstExp exp;
    
    public AstNewExpArray(AstType type, AstExp exp, int lineNum) {
        super(type, lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }
    
    public void printMe() {
        System.out.format("AST NODE NEW EXPRESSION ARRAY\n");

        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "NEW\nEXP\nARRAY");

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
