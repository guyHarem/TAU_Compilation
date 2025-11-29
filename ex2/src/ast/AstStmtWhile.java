package ast;

public class AstStmtWhile extends AstStmt
{
	public AstExp cond;
	public AstList<AstStmt> body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtWhile(AstExp cond, AstList<AstStmt> body, int lineNum)
	{
		super(lineNum);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
	}

	public void PrintMe()
	{
		System.out.print("AST NODE STMT WHILE\n");

		if (cond != null) cond.printMe();
		if (body != null) body.printMe();

		AstGraphviz.getInstance().logNode(
			serialNumber,
			"STMT\nWHILE");

		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);

	}
}