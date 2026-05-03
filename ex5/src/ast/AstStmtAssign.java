package ast;

import temp.*;
import types.*;

public class AstStmtAssign extends AstStmt {
    public AstVar var;
    public AstExp exp;

    public AstStmtAssign(AstVar var, AstExp exp, int line) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.exp = exp;
        this.line = line;
    }

    @Override
    public void printMe() {
        System.out.print("AST NODE ASSIGN STMT\n");
        if (var != null) var.printMe();
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := right\n");
        AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public Type semantMe() {
        Type varType = null;
        Type expType = null;

        if (var != null) varType = var.semantMe();
        if (exp != null) expType = exp.semantMe();

        if (!isAssignable(varType, expType)) {
            throw new SemanticError(line, "type mismatch for var := exp");
        }
        return null;
    }

    private boolean isAssignable(Type varType, Type expType) {
        if (varType == expType) return true;
        if (expType instanceof TypeNil) {
            return (varType instanceof TypeClass) || (varType instanceof TypeArray);
        }
        if (varType instanceof TypeClass && expType instanceof TypeClass) {
            return ((TypeClass)expType).isDescendantOf((TypeClass)varType);
        }
        if (varType instanceof TypeArray && expType instanceof TypeArray) {
            TypeArray varArr = (TypeArray) varType;
            TypeArray expArr = (TypeArray) expType;
            if (expArr.name.endsWith("[]") && varArr.elementType == expArr.elementType) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Temp irMe() {
        Temp src = exp.irMe();      
        
        if (var instanceof AstVarSimple) {
            AstVarSimple astVarSimple = (AstVarSimple) var;
            astVarSimple.doStore(src);
        } else var.irMe();
        return null;
    }
}
