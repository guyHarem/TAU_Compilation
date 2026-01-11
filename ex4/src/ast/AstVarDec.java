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
        Ir.getInstance().AddIrCommand(new IrCommandAllocate(unique_name));
        if (exp != null) {
            Ir.getInstance().AddIrCommand(new IrCommandStore(unique_name, exp.irMe()));
        }
        return null;
    }
}
