package ast;

import types.*;
import symboltable.*;

public class AstStmtReturn extends AstStmt
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtReturn(AstExp exp, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.exp = exp;
		this.line = line;
	}

	/********************************************************/
	/* The printing message for a return statement AST node */
	/********************************************************/
	public void printMe()
	{
		/***********************************/
		/* AST NODE TYPE = AST RETURN STMT */
		/***********************************/
		System.out.print("AST NODE STMT RETURN\n");

		/*****************************/
		/* RECURSIVELY PRINT exp ... */
		/*****************************/
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"RETURN");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	@Override
	public Type semantMe()
	{
		/******************************************/
		/* [1] Get the current function's type    */
		/******************************************/
		TypeFunction currentFunc = SymbolTable.getInstance().getCurrentFunction();
		if (currentFunc == null) {
			throw new SemanticError(line, "return statement outside of function");
		}
		Type expectedReturnType = currentFunc.returnType;

		/******************************************/
		/* [2] Check return expression type       */
		/******************************************/
		if (exp == null) {
			// return; is only valid for void functions
			if (expectedReturnType != null && expectedReturnType != TypeVoid.getInstance()) {
				throw new SemanticError(line, "non-void function must return a value");
			}
		} else {
			Type actualReturnType = exp.semantMe();
			// Void function should not return a value
			if (expectedReturnType == null || expectedReturnType == TypeVoid.getInstance()) {
				throw new SemanticError(line, "void function cannot return a value");
			}
			// Check type compatibility
			if (!isReturnCompatible(expectedReturnType, actualReturnType)) {
				throw new SemanticError(line, "return type mismatch");
			}
		}

		return null;
	}

	private boolean isReturnCompatible(Type expected, Type actual) {
		if (expected == actual) return true;

		// nil can be returned for class or array
		if (actual instanceof TypeNil) {
			return (expected instanceof TypeClass) || (expected instanceof TypeArray);
		}

		// Subclass can be returned as parent class
		if (expected instanceof TypeClass && actual instanceof TypeClass) {
			return ((TypeClass)actual).isDescendantOf((TypeClass)expected);
		}

		// Array: check element type
		if (expected instanceof TypeArray && actual instanceof TypeArray) {
			return ((TypeArray)expected).elementType == ((TypeArray)actual).elementType;
		}

		return false;
	}
}
