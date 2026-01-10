package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstVarSimple extends AstVar {
    public String name;

    public AstVarSimple(String name, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.line = lineNum;
    }

	@Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("var (%s)", name));
    }

    @Override
    public Type semantMe() {
        // Get current class context (if inside a class method)
        TypeClass currentClass = SymbolTable.getInstance().getCurrentClass();

        // First check all local scopes (excluding global)
        // This covers: block scopes, method scope, class scope (own members)
        Type localT = SymbolTable.getInstance().findExcludingGlobal(name);
        if (localT != null) {
            return localT;
        }

        // If inside a class, check class members (including inherited) before global
        // This implements spec 2.7: class scope -> superclass chain -> global
        if (currentClass != null) {
            Type memberType = currentClass.findMember(name);
            if (memberType != null) {
                return memberType;
            }
        }

        // Finally check global scope
        Type t = SymbolTable.getInstance().find(name);
        if (t != null) {
            return t;
        }

        throw new SemanticError(line, "undefined variable: " + name);
    }
    
    public Temp irMe()
    {
        Temp t = TempFactory.getInstance().getFreshTemp();
        // The static analyzer needs to know WHICH 'x' this is.
        // If your symbol table tracks offsets, pass that here.
        Ir.getInstance().AddIrCommand(new IrCommandLoad(t, name));
        return t;
    }
}
