package ast;

import symboltable.*;
import temp.*;
import types.*;

public class AstArrayTypeDef extends AstDec {
    public String name;
    public AstType type;

    public AstArrayTypeDef(String name, AstType type, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.type = type;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        String output = String.format("def array (%s)", name);
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, output);
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }

    @Override
    public Type semantMe() {
        /******************************************/
        /* [1] Look up the element type           */
        /******************************************/
        Type elementType = SymbolTable.getInstance().find(type.name);
        if (elementType == null) {
            throw new SemanticError(line, "undefined element type: " + type.name);
        }

        /******************************************/
        /* [1.5] Check element type is not void   */
        /* Spec 2.3: Arrays defined over non-void */
        /******************************************/
        if (elementType == TypeVoid.getInstance()) {
            throw new SemanticError(type.line, "array cannot have void element type");
        }

        /******************************************/
        /* [1.6] Check for duplicate name         */
        /******************************************/
        Type existingType = SymbolTable.getInstance().find(name);
        if (existingType != null) {
            throw new SemanticError(line, "identifier already defined: " + name);
        }

        /******************************************/
        /* [2] Create and register the array type */
        /******************************************/
        TypeArray arrayType = new TypeArray(name, elementType);
        SymbolTable.getInstance().enter(name, arrayType);

        return arrayType;
    }
    
    @Override
    public Temp irMe() {
        return null;
    }
}
