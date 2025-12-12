package ast;

import types.*;
import symboltable.*;

public class AstNewExp extends AstExp {
    public AstType type;

    public AstNewExp(AstType type, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "new");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }

    @Override
    public Type semantMe() {
        /******************************************/
        /* [1] Look up the type name              */
        /******************************************/
        Type t = SymbolTable.getInstance().find(type.name);
        if (t == null) {
            throw new SemanticError(line, "undefined type: " + type.name);
        }

        /******************************************/
        /* [2] Must be a class type               */
        /******************************************/
        if (!(t instanceof TypeClass)) {
            throw new SemanticError(line, "new requires class type");
        }

        return t;
    }
}
