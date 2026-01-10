package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstStmtWhile extends AstStmt {
    public AstExp cond;
    public AstList<AstStmt> body;

    public AstStmtWhile(AstExp cond, AstList<AstStmt> body, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;
        this.line = lineNum;
    }

	@Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (body != null) body.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "while");
        if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public Type semantMe() {
        /****************************/
        /* [1] Semant the condition */
        /****************************/
        Type condType = cond.semantMe();
        if (condType != TypeInt.getInstance()) {
            throw new SemanticError(cond.line, "while condition must be int");
        }

        /*************************/
        /* [2] Begin While Scope */
        /*************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [3] Semant Body         */
        /***************************/
        if (body != null) {
            body.semantMe();
        }

        /*****************/
        /* [4] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        return null;
    }

	public Temp irMe()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String labelEnd   = IrCommand.getFreshLabel("end");
		String labelStart = IrCommand.getFreshLabel("start");

		/*********************************/
		/* [2] entry label for the while */
		/*********************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelStart));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		Temp condTemp = cond.irMe();

		/******************************************/
		/* [4] Jump conditionally to the loop end */
		/******************************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelEnd));

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.irMe();

		/******************************/
		/* [6] Jump to the loop entry */
		/******************************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandJumpLabel(labelStart));

		/**********************/
		/* [7] Loop end label */
		/**********************/
		Ir.
				getInstance().
				AddIrCommand(new IrCommandLabel(labelEnd));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}
}