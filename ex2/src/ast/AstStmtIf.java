package ast;

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstList<AstStmt> body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstList<AstStmt> body, int lineNum)
	{
		super(lineNum);
		this.cond = cond;
		this.body = body;
	}

	public void printMe()
	{
		System.out.print("AST NODE STMT IF\n");

		if (cond != null) cond.printMe();
		if (body != null) body.printMe();

		AstGraphviz.getInstance().logNode(
			serialNumber,
			"STMT\nIF");

		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
	}
}