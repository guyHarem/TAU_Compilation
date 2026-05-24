package ast;

import ir.*;
import temp.*;
import types.*;

public class AstVarField extends AstVar {
    public AstVar var;
    public String fieldName;

    // Cached during semantMe so irMe doesn't need to re-resolve.
    private TypeClass baseType;
    private int fieldOffset;

    public AstVarField(AstVar var, String fieldName, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.fieldName = fieldName;
        this.line = lineNum;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("field (%s)", fieldName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }

    @Override
    public Type semantMe() {
        // Get the type of the base variable
        Type varType = var.semantMe();

        // Check that the base is a class type
        if (!(varType instanceof TypeClass)) {
            throw new SemanticError(line, "field access on non-class type");
        }

        // Look up the field in the class hierarchy
        TypeClass classType = (TypeClass) varType;
        Type fieldType = classType.findMember(fieldName);

        if (fieldType == null) {
            throw new SemanticError(line, "undefined field: " + fieldName);
        }

        // Cache for codegen.
        this.baseType = classType;
        this.fieldOffset = classType.getFieldOffset(fieldName);

        return fieldType;
    }

    public int getFieldOffset() { return fieldOffset; }

    // Load `obj.field`: nil-check the receiver, then lw at the cached offset.
    public Temp doLoad() {
        Temp base = var.irMe();
        Ir.getInstance().AddIrCommand(new IrCommandNilCheck(base));
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadField(dst, base, fieldOffset));
        return dst;
    }

    public void doStore(Temp src) {
        Temp base = var.irMe();
        Ir.getInstance().AddIrCommand(new IrCommandNilCheck(base));
        Ir.getInstance().AddIrCommand(new IrCommandStoreField(src, base, fieldOffset));
    }

    @Override
    public Temp irMe() {
        return doLoad();
    }
}
