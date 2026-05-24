package ast;

import ir.*;
import temp.*;
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

    @Override
    public Temp irMe() {
        Type t = SymbolTable.getInstance().find(type.name);
        if (!(t instanceof TypeClass)) {
            throw new RuntimeException("AstNewExp.irMe expects a class type, got: " + type.name);
        }
        TypeClass classType = (TypeClass) t;

        // 1. Allocate via the malloc helper. Going through IrCommandCall gives
        //    proper caller-save and $ra preservation.
        Temp sizeT = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(sizeT, classType.getInstanceSize()));
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandCall(dst, null, mips.MipsGenerator.LABEL_MALLOC, new Temp[]{sizeT}));

        // 2. Write vtable pointer at offset 0.
        Temp vtbl = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(vtbl, "vtable_" + type.name));
        Ir.getInstance().AddIrCommand(new IrCommandStoreAt(dst, vtbl));

        // 3. Per-instance literal initializers. malloc already zeroed memory
        //    so nil/zero inits are no-ops.
        for (TypeClass.FieldInit fi : classType.getAllFieldInits()) {
            if (fi.intValue != null) {
                if (fi.intValue == 0) continue;
                Temp v = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new ir.IrCommandConstInt(v, fi.intValue));
                Ir.getInstance().AddIrCommand(new ir.IrCommandStoreField(v, dst, fi.offset));
            } else if (fi.stringLabel != null) {
                String label = ir.IrCommand.getFreshAnonymousLabel();
                Ir.getInstance().AddIrGlobalDecleration(new ir.IrCommandStringLabel(label, fi.stringLabel));
                Temp v = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new ir.IrCommandLoadAddress(v, label));
                Ir.getInstance().AddIrCommand(new ir.IrCommandStoreField(v, dst, fi.offset));
            }
        }
        return dst;
    }
}
