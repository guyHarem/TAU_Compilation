package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstList<AstStmt> body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstList<AstStmt> body, int lineNum)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.cond = cond;
		this.body = body;
		this.line = lineNum;
	}

	/****************************************************/
	/* The printing message for an if statment AST node */
	/****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT IF\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (cond != null) cond.printMe();
		if (body != null) body.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"IF (left)\nTHEN right");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}

	public Type semantMe()
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.semantMe() != TypeInt.getInstance())
		{
			throw new SemanticError(cond.line, "if condition must be int");
		}

		/*************************/
		/* [1] Begin If Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.semantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/***************************************************/
		/* [4] Return value is irrelevant for if statement */
		/**************************************************/
		return null;
	}

	@Override
	public Temp irMe()
	{
		String labelEnd = IrCommand.getFreshLabel("end");
		Temp condTemp = cond.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelEnd));
		body.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));
		return null;
	}
}
