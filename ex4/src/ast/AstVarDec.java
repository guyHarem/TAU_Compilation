package ast;

import types.*;
import symboltable.*;

public class AstVarDec extends AstDec {
    public AstType type;
    public String name;
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
        /*********************************/
        /* [1] Look up the declared type */
        /*********************************/
        Type varType = SymbolTable.getInstance().find(type.name);
        if (varType == null) {
            throw new SemanticError(type.line, "undefined type: " + type.name);
        }

        /*****************************************/
        /* [1.5] Check that type is not void     */
        /* Spec 2.1: Variables cannot have void  */
        /*****************************************/
        if (varType == TypeVoid.getInstance()) {
            throw new SemanticError(type.line, "variable cannot have void type");
        }

        /*****************************************/
        /* [2] Check for duplicate in same scope */
        /*****************************************/
        if (SymbolTable.getInstance().findInCurrentScope(name) != null) {
            throw new SemanticError(type.line, "variable already defined in scope: " + name);
        }

        /**************************************/
        /* [3] Check initializer if present   */
        /**************************************/
        if (exp != null) {
            Type expType = exp.semantMe();
            // Type compatibility check
            if (!isAssignable(varType, expType)) {
                throw new SemanticError(line, "type mismatch in variable initialization");
            }
        }

        /*************************************/
        /* [4] Enter variable to symbol table */
        /*************************************/
        SymbolTable.getInstance().enter(name, varType);

        return varType;
    }

    private boolean isAssignable(Type varType, Type expType) {
        if (varType == expType) return true;

        // nil can be assigned to class or array
        if (expType instanceof TypeNil) {
            return (varType instanceof TypeClass) || (varType instanceof TypeArray);
        }

        // Class: subclass can be assigned to parent
        if (varType instanceof TypeClass && expType instanceof TypeClass) {
            return ((TypeClass)expType).isDescendantOf((TypeClass)varType);
        }

        // Array: check element type
        if (varType instanceof TypeArray && expType instanceof TypeArray) {
            return ((TypeArray)varType).elementType == ((TypeArray)expType).elementType;
        }

        return false;
    }
}
