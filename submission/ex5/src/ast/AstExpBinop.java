package ast;

import ir.*;
import mips.MipsGenerator;
import temp.*;
import types.*;

public class AstExpBinop extends AstExp
{
	int op;
	public AstExp left;
	public AstExp right;
	private int opType; // 0 - int _op_ int. 1 - string _op_ string. 2 - class/object _op_ class/object (?).
	
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
				this.opType = 0;
				return TypeInt.getInstance();
			}
			if ((t1 == TypeString.getInstance()) && (t2 == TypeString.getInstance()))
			{
				this.opType = 1;
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
				this.opType = 0;
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
				this.opType = 0;
				return TypeInt.getInstance();
			}
			throw new SemanticError(line, "comparison requires int operands");
		}

		/******************************************/
		/* Equality: 6 = EQ                       */
		/* For int, string, class, array          */
		/******************************************/
		if (op == 6) {
			this.opType = 0; // Comparision always returns int.
			
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
		if (this.opType != 0) return null; // For now only handle int operations (TODO).

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

        // Recursively generate IR for left and right expressions
        if (left  != null) t1 = left.irMe();
        if (right != null) t2 = right.irMe();

		if (this.opType == 0 /* Int OP */) {
            // Map op IDs to corresponding IR commands
            switch (op) {
                case 0:
                    // Addition
                    Ir.getInstance().AddIrCommand(new IrCommandBinopAddIntegers(dst, t1, t2));
                    break;
                case 1:
                    // Subtraction - Missing in your previous version
                    Ir.getInstance().AddIrCommand(new IrCommandBinopSubIntegers(dst, t1, t2));
                    break;
                case 2:
                    // Multiplication
                    Ir.getInstance().AddIrCommand(new IrCommandBinopMulIntegers(dst, t1, t2));
                    break;
                case 3:
                    // Division - Note: semantMe uses 3 for Division, but your previous irMe used 3 for Equality
                    Ir.getInstance().AddIrCommand(new IrCommandBinopDivIntegers(dst, t1, t2));
                    break;
                case 4:
                    // Less Than
                    Ir.getInstance().AddIrCommand(new IrCommandBinopLtIntegers(dst, t1, t2));
                    break;
                case 5:
                    // Greater Than - Missing in your previous version
                    Ir.getInstance().AddIrCommand(new IrCommandBinopGtIntegers(dst, t1, t2));
                    break;
                case 6:
                    // Equality - In semantMe, 6 is used for EQ
                    Ir.getInstance().AddIrCommand(new IrCommandBinopEqIntegers(dst, t1, t2));
                    break;
                default:
                    throw new UnsupportedOperationException("irMe: BinOp [Integer] IR not implemented for op ID: " + op);
            }
		} else if (this.opType == 1 /* String Op */) {
            switch (op) {
                case 0: // Addition
					Ir.getInstance().AddIrCommand(new IrCommandCall(dst, null, MipsGenerator.LABEL_STR_CONCAT, new Temp[]{t1, t2}));
                    break;
				// For now we don't support any other option.
                case 6:
                    throw new UnsupportedOperationException("irMe: BinOp [String] IR not implemented for op ID: " + op);
                default:
                    throw new UnsupportedOperationException("irMe: BinOp [String] IR not implemented for op ID: " + op);
            }
		} else throw new UnsupportedOperationException("irMe: BinOp [Object] IR not implemented for op ID: " + op);
        return dst;
    }
}
