package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarSimple extends AstVar {
    public String name;
    public String unique_name;
    private int caseType; // 1: Local, 2: Field, 3: Global
    private int fieldOffset; // Only for Case 2
    private Temp localTemp; // Only for Case 1

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

        if (foundEntry == null) foundEntry = sym.findEntry(name);
        this.unique_name = name + "@" + foundEntry.scopeLevel;
        return foundType;
    }

    @Override
    public Temp irMe() {
        if (this.isGlobal) {
            Temp t = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandLoad(t, unique_name));
            return t;
        } else return this.localTemp;
    }
}
