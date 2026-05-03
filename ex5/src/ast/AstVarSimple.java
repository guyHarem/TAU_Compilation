package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarSimple extends AstVar {
    public String name;
    public String unique_name;
    public int caseType; // 1: Local, 2: Field, 3: Global
    private int fieldOffset; // Only for Case 2
    public Temp localTemp; // Only for Case 1

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
        // We must find the correct type AND the unique scope level.
        // We store the found type in a variable so we can "bake" the 
        // unique_name before returning, regardless of which scope matched.
        SymbolTableEntry foundEntry;
        Type foundType = null;
        SymbolTable sym = SymbolTable.getInstance();

        // 1. Get current class context (if inside a class method)
        TypeClass currentClass = sym.getCurrentClass();

        // 2. Check all local scopes (excluding global)
        if ((foundEntry = sym.findEntryExcludingGlobal(name)) != null) {
            foundType = foundEntry.type;
            this.caseType = 1;
            this.localTemp = foundEntry.temp;
        }

        // 3. If not found, check class members (if applicable)
        if (foundType == null && currentClass != null) {
            if ((foundType = currentClass.findMember(name)) != null) {
                this.caseType = 2;
                this.fieldOffset = currentClass.getFieldOffset(name);
                this.unique_name = name + "@Field"; // There can't be class inside class so its ok.
                return foundType;
            }
        }

        // 4. Finally, check global scope
        if (foundType == null) {
            if ((foundType = sym.find(name)) != null) {
                this.caseType = 3;
            }
        }

        // 5. Validation
        if (foundType == null) {
            throw new SemanticError(line, "undefined variable: " + name);
        }

        if (foundEntry == null) foundEntry = sym.findEntry(name); // Applies to both local and global scopes.
        this.unique_name = name + "@" + foundEntry.scopeLevel;
        return foundType;
    }

    public Temp doLoad() {
        Temp t;
        switch (this.caseType) {
            case 1: // LOCAL
                return this.localTemp;
            case 2: // FIELD
                t = TempFactory.getInstance().getFreshTemp();
                Temp thisPtr = SymbolTable.getInstance().currThis;
                Ir.getInstance().AddIrCommand(new IrCommandLoadField(t, thisPtr, this.fieldOffset));
                return t;
            case 3: // GLOBAL
                t = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandLoadGlobal(t, this.unique_name));
                return t;
                
            default:
                return null;
        }
    }

    public void doStore(Temp src) {
        Temp t;
        switch (this.caseType) {
            case 1: // LOCAL
                Ir.getInstance().AddIrCommand(new IrCommandMove(this.localTemp, src));
            case 2: // FIELD
                Ir.getInstance().AddIrCommand(new IrCommandStoreField(src, this.localTemp, this.fieldOffset));
            case 3: // GLOBAL
                Ir.getInstance().AddIrCommand(new IrCommandStoreGlobal(this.unique_name, src));
        }
    }

    @Override
    public Temp irMe() {
        return doLoad();
    }
}
