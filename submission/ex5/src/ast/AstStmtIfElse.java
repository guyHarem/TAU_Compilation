package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstStmtIfElse extends AstStmt {
    public AstExp cond;
    public AstList<AstStmt> body;
    public AstList<AstStmt> elseBody;

    public AstStmtIfElse(AstExp cond, AstList<AstStmt> body, AstList<AstStmt> elseBody, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;
        this.elseBody = elseBody;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (body != null) body.printMe();
        if (elseBody != null) elseBody.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "if-else stmt");
        if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
    }

    @Override
    public Type semantMe() {
        /****************************/
        /* [1] Semant the condition */
        /****************************/
        Type condType = cond.semantMe();
        if (condType != TypeInt.getInstance()) {
            throw new SemanticError(cond.line, "if condition must be int");
        }

        /*************************/
        /* [2] Begin If Scope    */
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

        /***************************/
        /* [5] Begin Else Scope    */
        /***************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [6] Semant Else Body    */
        /***************************/
        if (elseBody != null) {
            elseBody.semantMe();
        }

        /*****************/
        /* [7] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        return null;
    }

	@Override
	public Temp irMe()
	{
		String labelElse = IrCommand.getFreshLabel("else");
		String labelEnd = IrCommand.getFreshLabel("end");
		Temp condTemp = cond.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));
		body.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelEnd));
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelElse));
		elseBody.irMe();
		Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));
		return null;
	}
}
