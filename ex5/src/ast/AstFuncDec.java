package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;

public class AstFuncDec extends AstDec {
    public AstType type;
    public String name;
    public AstList<AstVarDec> params;
    public AstList<AstStmt> body;

    public AstFuncDec(AstType type, String name, AstList<AstVarDec> params, AstList<AstStmt> body, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        if (params != null) params.printMe();
        if (body != null) body.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("def func (%s)", name));
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (params != null) AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
        if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public Type semantMe() {
        Type returnType = null;
        TypeList paramTypes = null;

        /*******************/
        /* [1] Return type */
        /*******************/
        if (type != null) {
            returnType = SymbolTable.getInstance().find(type.name);
            if (returnType == null) {
                throw new SemanticError(line, "non-existing return type: " + type.name);
            }
        }

        /*****************************************************/
        /* [1.5] Check for duplicate in current scope only   */
        /*       (class method can shadow global function)   */
        /*****************************************************/
        int errorLine = (type != null) ? type.line : line;
        Type existingType = SymbolTable.getInstance().findInCurrentScope(name);
        if (existingType != null) {
            throw new SemanticError(errorLine, "identifier already defined: " + name);
        }

        /*****************************************************/
        /* [2] Enter function to symbol table BEFORE body    */
        /*     (to allow recursive calls)                    */
        /*****************************************************/
        TypeFunction funcType = new TypeFunction(returnType, name, null);
        SymbolTable.getInstance().enter(name, funcType);

        /****************************/
        /* [3] Begin Function Scope */
        /****************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [4] Semant Input Params */
        /***************************/
        TypeList lastParam = null;
        if (params != null) {
            for (AstList<AstVarDec> it = params; it != null; it = it.tail) {
                if (it.head != null) {
                    Type paramType = it.head.semantMe();
                    TypeList newParam = new TypeList(paramType, null);
                    if (lastParam == null) {
                        paramTypes = newParam;
                    } else {
                        lastParam.tail = newParam;
                    }
                    lastParam = newParam;
                }
            }
        }

        // Update function type with parameter list
        funcType.params = paramTypes;

        /*****************************************/
        /* [4.5] Set current function context    */
        /*****************************************/
        SymbolTable.getInstance().setCurrentFunction(funcType);

        /*******************/
        /* [5] Semant Body */
        /*******************/
        if (body != null) {
            for (AstList<AstStmt> it = body; it != null; it = it.tail) {
                if (it.head != null) {
                    it.head.semantMe();
                }
            }
        }

        /*****************************************/
        /* [5.5] Clear current function context  */
        /*****************************************/
        SymbolTable.getInstance().setCurrentFunction(null);

        /*****************/
        /* [6] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        return funcType;
    }
	
	@Override
    public Temp irMe() {
        Ir.getInstance().AddIrCommand(new IrCommandFuncStart(name));

        // If in a class, $a0 is 'this'. Capture it first.
        if (SymbolTable.getInstance().getCurrentClass() != null) {
            Temp thisTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandMoveFromReg("$a0", thisTemp));
            SymbolTable.getInstance().currThis = thisTemp;
        }

        // Move parameters from $a0-a3 (or stack) into their corresponding Temps
        if (params != null) {
            int i = (SymbolTable.getInstance().getCurrentClass() != null) ? 1 : 0;
            for (AstList<AstVarDec> it = params; it != null; it = it.tail) {
                if (it.head != null) {
                    Temp pTemp = it.head.irMe();
                    if (i < 4) Ir.getInstance().AddIrCommand(new IrCommandMoveFromReg("$a" + i, pTemp));
                    else {
                        int offset = (i - 4) * 4;
                        Ir.getInstance().AddIrCommand(new IrCommandLoadFromReg(pTemp, "$sp", offset));
                    }
                    i++;
                }
            }
        }

        if (body != null) body.irMe();
        if (this.type.name.equals("void")) Ir.getInstance().AddIrCommand(new IrCommandReturn(null));
        Ir.getInstance().AddIrCommand(new IrCommandFuncEnd(name + "_END"));
        return null;
    }
}
