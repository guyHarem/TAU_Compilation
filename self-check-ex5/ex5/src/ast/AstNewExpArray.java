package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;
import mips.*;

public class AstNewExpArray extends AstNewExp {
    public AstExp exp;

    public AstNewExpArray(AstType type, AstExp exp, int lineNum) {
        super(type, lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "new array");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public Type semantMe() {
        /******************************************/
        /* [1] Look up the element type           */
        /******************************************/
        Type elementType = SymbolTable.getInstance().find(type.name);
        if (elementType == null) {
            throw new SemanticError(line, "undefined type: " + type.name);
        }

        /******************************************/
        /* [1.5] Check element type is not void   */
        /******************************************/
        if (elementType == TypeVoid.getInstance()) {
            throw new SemanticError(line, "cannot allocate array of void");
        }

        /******************************************/
        /* [2] Size expression must be int        */
        /******************************************/
        if (exp != null) {
            Type sizeType = exp.semantMe();
            if (sizeType != TypeInt.getInstance()) {
                throw new SemanticError(line, "array size must be int");
            }
            // Check for constant size <= 0 (including expressions like 0-5)
            Integer constantSize = exp.evaluateConstant();
            if (constantSize != null && constantSize <= 0) {
                throw new SemanticError(exp.line, "array size must be greater than 0");
            }
        }

        /******************************************/
        /* [3] Return a TypeArray for this        */
        /******************************************/
        return new TypeArray(type.name + "[]", elementType);
    }

    @Override
    public Temp irMe() {
        Temp size = exp.irMe();
        Temp address = TempFactory.getInstance().getFreshTemp();
        // label_alloc_array does the (size+1)*4 byte allocation, the size>0 check,
        // stores length at offset 0, and zero-fills the data area.
        Ir.getInstance().AddIrCommand(new IrCommandCall(address, null, MipsGenerator.LABEL_ALLOC_ARRAY, new Temp[]{size}));
        return address;
    }
}
