package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarSimple extends AstVar {
    public String name;
    public String unique_name;
    public int caseType; // 1: Local/Param, 2: Field, 3: Global
    private int fieldOffset; // Only for Case 2
    private int slotIndex = -1; // Only for Case 1
    public Temp localTemp; // legacy, unused — kept to minimize touches elsewhere

    public AstVarSimple(String name, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.line = lineNum;
        this.name = name;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("var (%s)", name));
    }

    @Override
    public Type semantMe() {
        SymbolTableEntry foundEntry;
        Type foundType = null;
        SymbolTable sym = SymbolTable.getInstance();

        TypeClass currentClass = sym.getCurrentClass();

        // 1. Check function/block scopes (excluding class fields and globals)
        if ((foundEntry = sym.findEntryExcludingGlobal(name)) != null) {
            foundType = foundEntry.type;
            this.caseType = 1;
            this.slotIndex = foundEntry.slotIndex;
        }

        // 2. If not found, check class members (if applicable)
        if (foundType == null && currentClass != null) {
            if ((foundType = currentClass.findMember(name)) != null) {
                this.caseType = 2;
                this.fieldOffset = currentClass.getFieldOffset(name);
                this.unique_name = name + "@Field";
                return foundType;
            }
        }

        // 3. Finally, check global scope
        if (foundType == null) {
            if ((foundType = sym.find(name)) != null) {
                this.caseType = 3;
            }
        }

        if (foundType == null) {
            throw new SemanticError(line, "undefined variable: " + name);
        }

        if (foundEntry == null) foundEntry = sym.findEntry(name);
        this.unique_name = name + "@" + foundEntry.scopeLevel;
        return foundType;
    }

    public Temp doLoad() {
        Temp t;
        switch (this.caseType) {
            case 1: // LOCAL / PARAM (stack-resident)
                t = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandLoadLocal(t, this.slotIndex));
                return t;
            case 2: // FIELD (implicit `this.<name>`)
                t = TempFactory.getInstance().getFreshTemp();
                Temp thisPtr = SymbolTable.getInstance().currThis;
                Ir.getInstance().AddIrCommand(new IrCommandLoadField(t, thisPtr, this.fieldOffset));
                return t;
            case 3: // GLOBAL
                t = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandLoadGlobal(t, this.name));
                return t;
            default:
                return null;
        }
    }

    public void doStore(Temp src) {
        switch (this.caseType) {
            case 1: // LOCAL / PARAM
                Ir.getInstance().AddIrCommand(new IrCommandStoreLocal(src, this.slotIndex));
                break;
            case 2: // FIELD via implicit `this`
                Temp thisPtr = SymbolTable.getInstance().currThis;
                Ir.getInstance().AddIrCommand(new IrCommandNilCheck(thisPtr));
                Ir.getInstance().AddIrCommand(new IrCommandStoreField(src, thisPtr, this.fieldOffset));
                break;
            case 3: // GLOBAL
                Ir.getInstance().AddIrCommand(new IrCommandStoreGlobal(this.name, src));
                break;
        }
    }

    @Override
    public Temp irMe() {
        return doLoad();
    }
}
