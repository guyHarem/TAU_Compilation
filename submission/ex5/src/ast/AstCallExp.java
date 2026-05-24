package ast;

import ir.*;
import mips.MipsGenerator;
import symboltable.*;
import temp.*;
import types.*;

public class AstCallExp extends AstExp {
    public AstVar var;
    public Type retType;
    public String funcName;
    public AstList<AstExp> args;

    public AstCallExp(AstVar var, String funcName, AstList<AstExp> args, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.funcName = funcName;
        this.args = args;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (var != null) var.printMe();
        if (args != null) args.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("call (%s)", funcName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (args != null) AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }

    @Override
    public Type semantMe() {
        TypeFunction funcType = null;

        /******************************************/
        /* [1] Find the function/method           */
        /******************************************/
        if (var != null) {
            // Method call: var.funcName(args)
            Type varType = var.semantMe();
            if (!(varType instanceof TypeClass)) {
                throw new SemanticError(line, "method call on non-class type");
            }
            Type memberType = ((TypeClass)varType).findMember(funcName);
            if (memberType == null) {
                throw new SemanticError(line, "undefined method: " + funcName);
            }
            if (!(memberType instanceof TypeFunction)) {
                throw new SemanticError(line, funcName + " is not a method");
            }
            funcType = (TypeFunction) memberType;
        } else {
            // Function call: funcName(args)
            // Same resolution logic as AstVarSimple: local -> class members -> global
            TypeClass currentClass = SymbolTable.getInstance().getCurrentClass();
            SymbolTableEntry entry = SymbolTable.getInstance().findEntryExcludingGlobal(funcName);
            Type t;

            // First check local scopes (excluding global)
            t = (entry == null ? null : entry.type);

            // If not found locally and inside a class, check class members (including inherited)
            if (t == null && currentClass != null) {
                t = currentClass.findMember(funcName);
            }

            // Finally check global scope
            if (t == null) {
                t = SymbolTable.getInstance().find(funcName);
            }

            if (t == null) {
                throw new SemanticError(line, "undefined function: " + funcName);
            }
            if (!(t instanceof TypeFunction)) {
                throw new SemanticError(line, funcName + " is not a function");
            }
            funcType = (TypeFunction) t;
        }

        /******************************************/
        /* [2] Check argument types               */
        /******************************************/
        TypeList paramTypes = funcType.params;
        AstList<AstExp> argList = args;

        while (paramTypes != null && argList != null) {
            Type paramType = paramTypes.head;
            Type argType = argList.head.semantMe();

            // Check type compatibility
            if (!isAssignable(paramType, argType)) {
                throw new SemanticError(line, "argument type mismatch");
            }

            paramTypes = paramTypes.tail;
            argList = argList.tail;
        }

        // Check argument count matches
        if (paramTypes != null || argList != null) {
            throw new SemanticError(line, "wrong number of arguments");
        }

        /******************************************/
        /* [3] Return the function's return type  */
        /******************************************/
        this.retType = funcType.returnType;
        return funcType.returnType;
    }

    private boolean isAssignable(Type paramType, Type argType) {
        if (paramType == argType) return true;

        // nil can be assigned to class or array
        if (argType instanceof TypeNil) {
            return (paramType instanceof TypeClass) || (paramType instanceof TypeArray);
        }

        // Class: subclass can be passed to parent type parameter
        if (paramType instanceof TypeClass && argType instanceof TypeClass) {
            return ((TypeClass)argType).isDescendantOf((TypeClass)paramType);
        }

        // Array: Different named array types are NOT interchangeable.
        // But `new T[e]` creates an anonymous array that can be assigned to any matching array type.
        // Anonymous arrays have names like "int[]" (ending with "[]")
        if (paramType instanceof TypeArray && argType instanceof TypeArray) {
            TypeArray paramArr = (TypeArray) paramType;
            TypeArray argArr = (TypeArray) argType;
            // If source is anonymous (from new T[e]) and element types match, OK
            if (argArr.name.endsWith("[]") && paramArr.elementType == argArr.elementType) {
                return true;
            }
            // Otherwise must be exact same type (already handled above)
            return false;
        }

        return false;
    }

    public Temp irMe() {
        System.out.println("[DEBUG] AstCallExp func: " + this.funcName);
        Temp retval;
        Temp varTemp = (this.var == null ? null : this.var.irMe());

        // Evaluate arguments Left-to-Right
        int numArgs = 0;
        for (AstList<AstExp> it = this.args; it != null; it = it.tail) numArgs++;
        Temp[] argTemps = new Temp[numArgs];
        int i = 0;
        for (AstList<AstExp> it = this.args; it != null; it = it.tail) {
            argTemps[i++] = it.head.irMe();
        }

        // Handle Library syscalls
        if (this.funcName.equals("PrintInt")) {
            Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_PRINT_INT, argTemps));
            return null; // PrintInt is void
        }
        if (this.funcName.equals("PrintString")) {
            Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_PRINT_STRING, argTemps));
            return null; // PrintString is void
        }

        // General Function/Method Call
        // TODO: Check if void.
        if (this.retType.name.equals("void")) retval = null;
        else retval = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandCall(retval, varTemp, this.funcName, argTemps));
        return retval; 
    }
}
