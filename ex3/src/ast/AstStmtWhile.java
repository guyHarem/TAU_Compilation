package ast;

import types.*;
import symboltable.*;

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
}