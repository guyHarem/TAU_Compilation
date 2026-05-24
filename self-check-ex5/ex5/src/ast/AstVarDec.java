package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarDec extends AstDec {
    public AstType type;
    public String name;
    public String unique_name;
    public AstExp exp;
    public int caseType;       // 1: LOCAL/PARAM (stack), 2: FIELD, 3: GLOBAL
    public int slotIndex = -1; // for caseType==1: stack slot offset (in words)

    public AstVarDec(AstType type, String name, AstExp exp, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.exp = exp;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("def var (%s)", name));
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public Type semantMe() {
        SymbolTable sym = SymbolTable.getInstance();
        if (sym.currentScopeLevel == 0) this.caseType = 3; // GLOBAL
        else if (sym.getCurrentClass() != null && sym.currentScopeLevel == 1) this.caseType = 2; // FIELD
        else this.caseType = 1; // LOCAL/PARAM

        Type varType = SymbolTable.getInstance().find(type.name);
        if (varType == null) {
            throw new SemanticError(type.line, "undefined type: " + type.name);
        }

        if (varType == TypeVoid.getInstance()) {
            throw new SemanticError(type.line, "variable cannot have void type");
        }

        if (SymbolTable.getInstance().findInCurrentScope(name) != null) {
            throw new SemanticError(type.line, "variable already defined in scope: " + name);
        }

        if (exp != null) {
            Type expType = exp.semantMe();
            if (!isAssignable(varType, expType)) {
                throw new SemanticError(line, "type mismatch in variable initialization");
            }
        }

        SymbolTable.getInstance().enter(name, varType);

        SymbolTableEntry entry = SymbolTable.getInstance().findEntry(name);
        this.unique_name = name + "@" + entry.scopeLevel;

        // Stack slot for locals/params. slotIndex is a signed offset from $fp.
        if (this.caseType == 1) {
            TypeFunction currentFunc = sym.getCurrentFunction();
            if (currentFunc != null) {
                this.slotIndex = currentFunc.processingParams
                    ? currentFunc.allocateParamSlot()
                    : currentFunc.allocateLocalSlot();
                entry.slotIndex = this.slotIndex;
            }
        }
        return varType;
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
            return ((TypeArray)varType).elementType == ((TypeArray)expType).elementType;
        }
        return false;
    }

    @Override
    public Temp irMe() {
        if (this.caseType == 3 /* global */) {
            Ir.getInstance().AddIrGlobalDecleration(new IrCommandAllocate(name));
            if (exp != null) {
                Ir.getInstance().activeList = Ir.getInstance().globalInits;
                Ir.getInstance().AddIrCommand(new IrCommandStoreGlobal(name, exp.irMe()));
                Ir.getInstance().activeList = Ir.getInstance().commands;
            }
        } else if (this.caseType == 2 /* class field */) {
            // Field layout & per-instance init handled in AstNewExp.
        } else { // LOCAL/PARAM
            // Initialize slot to user's value if given, otherwise to 0
            // (which is also nil for refs).
            Temp src;
            if (exp != null) {
                src = exp.irMe();
            } else {
                src = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandConstInt(src, 0));
            }
            Ir.getInstance().AddIrCommand(new IrCommandStoreLocal(src, this.slotIndex));
        }
        return null;
    }
}
