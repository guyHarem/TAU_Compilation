package ast;

import temp.*;
import types.*;

public class AstExpVar extends AstExp
{
	public AstVar var;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpVar(AstVar var, int lineNum)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.var = var;
		this.line = lineNum;
	}

	/************************************************/
	/* The printing message for a var exp AST node */
	/************************************************/
	public void printMe()
	{
		if (var != null) var.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "var exp");
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
	}

	public Type semantMe()
	{
		if (var != null) return var.semantMe();
		return null;
	}

	@Override
	public Temp irMe() {
		if (var != null) {
			return var.irMe();
		}
		return null;
	}
}
