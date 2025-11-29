package ast;

public class AstStmtIfElse extends AstStmt {

    public AstExp cond;
	public AstList<AstStmt> body;
    public AstList<AstStmt> elseBody;
    
    public AstStmtIfElse(AstExp cond, AstList<AstStmt> body, AstList<AstStmt> elseBody, int lineNum) {
        super(lineNum);
        this.cond = cond;
        this.body = body;
        this.elseBody = elseBody;
    }

    public void printMe()
	{
		System.out.print("AST NODE STMT IF\n");

		if (cond != null) cond.printMe();
		if (body != null) body.printMe();
        if (elseBody != null) elseBody.printMe();

		AstGraphviz.getInstance().logNode(
			serialNumber,
			"STMT\nIF");

		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}
}
