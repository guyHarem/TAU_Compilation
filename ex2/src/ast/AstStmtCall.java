package ast;

public class AstStmtCall extends AstStmt {

    public AstCallExp exp;
    
    public AstStmtCall(AstCallExp exp ,int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.exp = exp;
    }
    
    public void PrintMe() {
        System.out.print("AST NODE STMT CALL\n");

        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "STMT CALL\n");

        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);

    }
}
