package ast;

public class AstCallExp extends AstExp {

    public AstVar var;
    public String varName; 
    public AstList<AstExp> args;
    
    public AstCallExp(AstVar var, String varName, AstList<AstExp> args, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.varName = varName;
        this.args = args;
    }

    public void printMe() {
        System.out.format("AST NODE CALL EXPRESSION( %s )\n", varName);

        if (var != null) var.printMe();
        if (args != null) args.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("CALL\nEXP\n(%s)", varName));

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (args != null)
            AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }
    
}
