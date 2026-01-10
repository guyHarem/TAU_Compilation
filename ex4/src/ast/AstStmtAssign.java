package ast;

import ir.*;
import temp.*;
import types.*;

public class AstStmtAssign extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstVar var;
	public AstExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssign(AstVar var, AstExp exp, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/*******************************/
		/* COPY INPUT DATA MENBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
		this.line = line;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void printMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"ASSIGN\nleft := right\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	public Type semantMe()
	{
		Type varType = null;
		Type expType = null;

		if (var != null) varType = var.semantMe();
		if (exp != null) expType = exp.semantMe();

		/******************************************/
		/* Check type compatibility               */
		/******************************************/
		if (!isAssignable(varType, expType)) {
			throw new SemanticError(line, "type mismatch for var := exp");
		}
		return null;
	}

	/******************************************/
	/* Check if expType can be assigned to varType */
	/******************************************/
	private boolean isAssignable(Type varType, Type expType) {
		// Same type - always OK
		if (varType == expType) {
			return true;
		}

		// nil can be assigned to class or array types
		if (expType instanceof TypeNil) {
			return (varType instanceof TypeClass) || (varType instanceof TypeArray);
		}

		// Class: subclass can be assigned to parent type
		if (varType instanceof TypeClass && expType instanceof TypeClass) {
			return ((TypeClass)expType).isDescendantOf((TypeClass)varType);
		}

		// Array: Different named array types are NOT interchangeable.
		// But `new T[e]` creates an anonymous array that can be assigned to any matching array type.
		// Anonymous arrays have names like "int[]" (ending with "[]")
		if (varType instanceof TypeArray && expType instanceof TypeArray) {
			TypeArray varArr = (TypeArray) varType;
			TypeArray expArr = (TypeArray) expType;
			// If source is anonymous (from new T[e]) and element types match, OK
			if (expArr.name.endsWith("[]") && varArr.elementType == expArr.elementType) {
				return true;
			}
			// Otherwise must be exact same type (already handled above)
			return false;
		}

		return false;
	}

	public Temp irMe()
	{
		System.out.println("[DEBUG] AstStmtAssign irMe");
		Temp src = exp.irMe();
		
		// For now, we only handle the 'Simple' case because Ex4 only uses int vars.
		if (var instanceof AstVarSimple) {
			String name = ((AstVarSimple) var).name;
			Ir.getInstance().AddIrCommand(new IrCommandStore(name, src));
		} else {
			// Placeholder for AstVarField and AstVarSubscript
			// These will be implemented in the final project.
			var.irMe(); 
		}

		return null;
	}
}
