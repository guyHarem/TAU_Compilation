package ast;

public class AstStmtVarDec extends AstStmt {
    public AstVarDec dec;

    public AstStmtVarDec(AstVarDec var, int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.dec = var;
    }

    /*********************************************************/
    /* The default message for a variable declaration AST node */
    /*********************************************************/
    public void PrintMe() {
        System.out.print("AST NODE VAR DEC\n");
        if (dec != null) dec.PrintMe();
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "DEC\nVAR DEC");
        if (dec != null)
        AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }
    
}
