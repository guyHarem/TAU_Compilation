package ast;

import temp.*;
import types.*;

public class AstStmtCall extends AstStmt
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstCallExp callExp;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstStmtCall(AstCallExp callExp, int lineNum)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.callExp = callExp;
		this.line = lineNum;
	}

	public void printMe()
	{
		if (callExp != null) callExp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("STMT\nCALL"));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (callExp != null) AstGraphviz.getInstance().logEdge(serialNumber,callExp.serialNumber);
	}

	@Override
	public Type semantMe()
	{
		if (callExp != null) {
			callExp.semantMe();
		}
		return null;
	}

	public Temp irMe()
	{
		if (callExp != null) return callExp.irMe();
		return null;
	}
}
