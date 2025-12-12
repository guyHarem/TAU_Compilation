package ast;

import types.*;
import symboltable.*;

public class AstDecClass extends AstDec
{
	/********/
	/* NAME */
	/********/
	public String name;

	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstTypeNameList dataMembers;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstDecClass(String name, AstTypeNameList dataMembers)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();
	
		this.name = name;
		this.dataMembers = dataMembers;
	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void printMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("CLASS DEC = %s\n",name);
		if (dataMembers != null) dataMembers.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("CLASS\n%s",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber, dataMembers.serialNumber);
	}
	
	public Type semantMe()
	{
		/*****************************************/
		/* [1] Create TypeClass and set context  */
		/*****************************************/
		TypeClass t = new TypeClass(null, name, null);
		SymbolTable.getInstance().setCurrentClass(t);

		/*************************/
		/* [2] Begin Class Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [3] Semant Data Members */
		/***************************/
		TypeList memberTypes = dataMembers.semantMe();
		t.dataMembers = memberTypes;

		/*****************/
		/* [4] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/******************************/
		/* [5] Clear class context    */
		/******************************/
		SymbolTable.getInstance().setCurrentClass(null);

		/************************************************/
		/* [6] Enter the Class Type to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(name, t);

		/*********************************************************/
		/* [7] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;
	}
}
