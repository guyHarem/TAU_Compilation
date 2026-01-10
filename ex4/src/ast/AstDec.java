package ast;

import temp.*;
import types.*;

public abstract class AstDec extends AstNode
{
	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe()
	{
		return null;
	}
	
	@Override
	public Temp irMe()
	{
		System.out.println("[DEBUG] AstDec irMe");
		return null;
	}
}
