package ast;

public abstract class AstStmt extends AstNode
{
    public AstStmt(int lineNum) {
		super(lineNum);
	}
	/*********************************************************/
	/* The default message for an unknown AST statement node */
	/*********************************************************/
	public void PrintMe()
	{
		System.out.print("UNKNOWN AST STATEMENT NODE");
	}
}
