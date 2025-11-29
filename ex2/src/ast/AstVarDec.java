package ast;

public class AstVarDec extends AstDec {

    public AstType type;
    public String name;
    public AstExp exp;

    public AstVarDec(AstType type, String name, AstExp exp, int lineNum) {
        super(lineNum);
        this.type = type;
        this.name = name;
        this.exp = exp;
    }

    /*********************************************************/
    /* The default message for an unknown AST var declaration node */
    /*********************************************************/
    public void PrintMe() {
        System.out.print("AST NODE VAR DEC\n");

        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("VAR\nDEC\n(%s)", name));

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
