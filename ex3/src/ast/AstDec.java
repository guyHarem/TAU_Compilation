package ast;

import types.*;

public abstract class AstDec extends AstNode
{
	public AstDec(int lineNum) {
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
