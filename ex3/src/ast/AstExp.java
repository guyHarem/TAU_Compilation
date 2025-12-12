package ast;

import types.*;

public abstract class AstExp extends AstNode
{
	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe()
	{
		return null;
	}

	/***********************************************/
	/* Evaluate constant expression. Returns null  */
	/* if expression is not a compile-time constant */
	/***********************************************/
	public Integer evaluateConstant()
	{
		return null;  // Override in subclasses
	}
}
