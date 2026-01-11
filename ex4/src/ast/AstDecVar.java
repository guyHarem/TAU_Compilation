package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstDecVar extends AstDec {
    public String type;
    public String name;
    public String unique_name;
    public AstExp initialValue;
    
    public AstDecVar(String type, String name, AstExp initialValue) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.initialValue = initialValue;
    }

    @Override
    public void printMe() {
        if (initialValue != null) System.out.format("VAR-DEC(%s):%s := initialValue\n", name, type);
        else System.out.format("VAR-DEC(%s):%s \n", name, type);

        if (initialValue != null) initialValue.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, String.format("VAR\nDEC(%s)\n:%s", name, type));

        if (initialValue != null) AstGraphviz.getInstance().logEdge(serialNumber, initialValue.serialNumber);
    }

    @Override
    public Type semantMe() {
        Type t = SymbolTable.getInstance().find(type);
        if (t == null) {
            throw new SemanticError(0, "non existing type " + type);
        }
        
        if (SymbolTable.getInstance().findInCurrentScope(name) != null) {
            throw new SemanticError(0, "variable " + name + " already exists in scope"); 
        }

        SymbolTable.getInstance().enter(name, t);

        SymbolTableEntry entry = SymbolTable.getInstance().findEntry(name);
        this.unique_name = name + "@" + entry.scopeLevel;
        return null;
    }

    @Override
    public Temp irMe() {
        Ir.getInstance().AddIrCommand(new IrCommandAllocate(unique_name));
        if (initialValue != null) {
            Ir.getInstance().AddIrCommand(new IrCommandStore(unique_name, initialValue.irMe()));
        }
        return null;
    }
}
