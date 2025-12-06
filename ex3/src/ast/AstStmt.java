package ast;

import types.*;

public abstract class AstStmt extends AstNode
{
	public AstStmt(int lineNum) {
        super(lineNum);
    }
	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe()
	{
		return null;
	}
}
