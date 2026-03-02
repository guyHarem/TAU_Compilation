package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarSimple extends AstVar {
    public String name;
    public String unique_name;

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
        Type foundType = null;

        // 1. Get current class context (if inside a class method)
        TypeClass currentClass = SymbolTable.getInstance().getCurrentClass();

        // 2. Check all local scopes (excluding global)
        foundType = SymbolTable.getInstance().findExcludingGlobal(name);

        // 3. If not found, check class members (if applicable)
        if (foundType == null && currentClass != null) {
            foundType = currentClass.findMember(name);
        }

        // 4. Finally, check global scope
        if (foundType == null) {
            foundType = SymbolTable.getInstance().find(name);
        }

        // 5. Validation
        if (foundType == null) {
            throw new SemanticError(line, "undefined variable: " + name);
        }

        SymbolTableEntry entry = SymbolTable.getInstance().findEntry(name);
        this.unique_name = name + "@" + entry.scopeLevel;
        return foundType;
    }

    @Override
    public Temp irMe() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoad(t, unique_name));
        return t;
    }
}
