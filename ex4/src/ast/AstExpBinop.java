package ast;

import types.*;
import temp.*;
import ir.*;

public class AstExpBinop extends AstExp
{
	int op;
	public AstExp left;
	public AstExp right;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpBinop(AstExp left, AstExp right, int op, int lineNum)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/*******************************/
		/* COPY INPUT DATA MENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.op = op;
		this.line = lineNum;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void printMe()
	{
		String sop="";
		
		/*********************************/
		/* CONVERT OP to a printable sop */
		/*********************************/
		if (op == 0) {sop = "+";}
		if (op == 1) {sop = "-";}
		if (op == 3) {sop = "=";}

		/**********************************/
		/* AST NODE TYPE = AST BINOP EXP */
		/*********************************/
		System.out.print("AST NODE BINOP EXP\n");
		System.out.format("BINOP EXP(%s)\n",sop);

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.printMe();
		if (right != null) right.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("BINOP(%s)",sop));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AstGraphviz.getInstance().logEdge(serialNumber,left.serialNumber);
		if (right != null) AstGraphviz.getInstance().logEdge(serialNumber,right.serialNumber);
	}

	public Type semantMe()
	{
		Type t1 = null;
		Type t2 = null;

		if (left  != null) t1 = left.semantMe();
		if (right != null) t2 = right.semantMe();

		/******************************************/
		/* Arithmetic operations: both must be int */
		/* 0=+, 1=-, 2=*, 3=/                     */
		/******************************************/
		if (op == 0) {
			// + can be int+int or string+string
			if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance()))
			{
				return TypeInt.getInstance();
			}
			if ((t1 == TypeString.getInstance()) && (t2 == TypeString.getInstance()))
			{
				return TypeString.getInstance();
			}
			throw new SemanticError(line, "plus operation requires int or string operands");
		}
		if (op == 1 || op == 2 || op == 3) {
			if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance()))
			{
				// Check for division by constant zero
				if (op == 3 && right instanceof AstExpInt) {
					if (((AstExpInt) right).value == 0) {
						throw new SemanticError(line, "division by zero");
					}
				}
				return TypeInt.getInstance();
			}
			throw new SemanticError(line, "arithmetic operation requires int operands");
		}

		/******************************************/
		/* Comparison operations: 4=<, 5=>        */
		/******************************************/
		if (op == 4 || op == 5) {
			if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance()))
			{
				return TypeInt.getInstance();
			}
			throw new SemanticError(line, "comparison requires int operands");
		}

		/******************************************/
		/* Equality: 6 = EQ                       */
		/* For int, string, class, array          */
		/******************************************/
		if (op == 6) {
			// int = int
			if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance()) {
				return TypeInt.getInstance();
			}
			// string = string (per spec Table 8, Example 4)
			if (t1 == TypeString.getInstance() && t2 == TypeString.getInstance()) {
				return TypeInt.getInstance();
			}
			// nil with class or array
			if (t1 instanceof TypeNil && (t2 instanceof TypeClass || t2 instanceof TypeArray)) {
				return TypeInt.getInstance();
			}
			if (t2 instanceof TypeNil && (t1 instanceof TypeClass || t1 instanceof TypeArray)) {
				return TypeInt.getInstance();
			}
			// nil = nil
			if (t1 instanceof TypeNil && t2 instanceof TypeNil) {
				return TypeInt.getInstance();
			}
			// Class comparison - must be same type or one is subclass of other
			if (t1 instanceof TypeClass && t2 instanceof TypeClass) {
				TypeClass c1 = (TypeClass) t1;
				TypeClass c2 = (TypeClass) t2;
				if (c1 == c2 || c1.isDescendantOf(c2) || c2.isDescendantOf(c1)) {
					return TypeInt.getInstance();
				}
				throw new SemanticError(line, "incompatible class types for equality comparison");
			}
			// Array comparison - must be exact same array type
			if (t1 instanceof TypeArray && t2 instanceof TypeArray) {
				if (t1 == t2) {
					return TypeInt.getInstance();
				}
				throw new SemanticError(line, "incompatible array types for equality comparison");
			}
			throw new SemanticError(line, "incompatible types for equality comparison");
		}

		throw new SemanticError(line, "unknown binary operator");
	}

	@Override
	public Integer evaluateConstant()
	{
		if (left == null || right == null) return null;

		Integer leftVal = left.evaluateConstant();
		Integer rightVal = right.evaluateConstant();

		if (leftVal == null || rightVal == null) return null;

		// Evaluate arithmetic operations on constants
		switch (op) {
			case 0: return leftVal + rightVal;  // +
			case 1: return leftVal - rightVal;  // -
			case 2: return leftVal * rightVal;  // *
			case 3: return rightVal != 0 ? leftVal / rightVal : null;  // /
			default: return null;
		}
	}

	public Temp irMe()
	{
		Temp t1 = null;
		Temp t2 = null;
		Temp dst = TempFactory.getInstance().getFreshTemp();

		if (left  != null) t1 = left.irMe();
		if (right != null) t2 = right.irMe();

		if (op == 0)
		{
			Ir.
					getInstance().
					AddIrCommand(new IrCommandBinopAddIntegers(dst,t1,t2));
		}
		if (op == 2)
		{
			Ir.
					getInstance().
					AddIrCommand(new IrCommandBinopMulIntegers(dst,t1,t2));
		}
		if (op == 3)
		{
			Ir.
					getInstance().
					AddIrCommand(new IrCommandBinopEqIntegers(dst,t1,t2));
		}
		if (op == 4)
		{
			Ir.
					getInstance().
					AddIrCommand(new IrCommandBinopLtIntegers(dst,t1,t2));
		}
		return dst;
	}
}
