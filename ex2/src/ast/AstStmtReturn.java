package ast;

public class AstStmtReturn extends AstStmt {

    public AstExp exp;
    
    public AstStmtReturn(AstExp exp, int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.exp = exp;
    }

    public void PrintMe() {
        System.out.print("AST NODE STMT RETURN\n");

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "STMT\nRETURN"
        );

        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
