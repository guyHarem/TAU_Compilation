package ast;

import temp.*;
import types.*;

public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;

	/*******************************************/
	/* Line number for semantic error reporting */
	/*******************************************/
	public int line;

	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	/***********************************************/
	/* Default semantic analysis (can be overridden) */
	/***********************************************/
	public Type semantMe()
	{
		return null;
	}

	/*****************************************/
	/* The default IR action for an AST node */
	/*****************************************/
	public Temp irMe()
	{
		throw new UnsupportedOperationException(
			"irMe not implemented for " + this.getClass().getSimpleName()
		);
	}
}