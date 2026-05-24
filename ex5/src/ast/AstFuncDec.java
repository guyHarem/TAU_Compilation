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
    public TypeFunction funcType; // settled in semantMe; reused by irMe for the stack frame

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
        this.funcType = funcType;

        /****************************/
        /* [3] Begin Function Scope */
        /****************************/
        SymbolTable.getInstance().beginScope();

        // Set current function before param semant so AstVarDec can allocate stack slots.
        SymbolTable.getInstance().setCurrentFunction(funcType);

        /***************************/
        /* [4] Semant Input Params */
        /***************************/
        TypeList lastParam = null;
        // Implicit `this` (for class methods) takes the first param slot ($fp+8).
        if (SymbolTable.getInstance().getCurrentClass() != null) {
            funcType.paramCount = 1;
            funcType.paramSlotCount = 1;
        }
        funcType.processingParams = true;
        if (params != null) {
            for (AstList<AstVarDec> it = params; it != null; it = it.tail) {
                if (it.head != null) {
                    Type paramType = it.head.semantMe();
                    funcType.paramCount++;
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
        funcType.processingParams = false;

        // Update function type with parameter list
        funcType.params = paramTypes;

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
        TypeFunction funcType = this.funcType;

        // SPIM enters at `main`. The user's main is renamed to _user_main.
        // _user_ prefix avoids collisions with SPIM reserved opcodes.
        String label;
        if (SymbolTable.getInstance().getCurrentClass() != null) {
            label = "_user_" + SymbolTable.getInstance().getCurrentClass().name + "_" + this.name;
        } else if (this.name.equals("main")) {
            label = "_user_main";
        } else {
            label = "_user_" + this.name;
        }
        Ir.getInstance().AddIrCommand(new IrCommandFuncStart(label, funcType));

        SymbolTable.getInstance().setCurrentFunction(funcType);

        // The prologue (in IrCommandFuncStart.mipsMe) spills $a0..$a3 into the
        // shadow space at $fp+8..+20, so params 0..3 are already in their slots.
        // Args 4+ were written by the caller above the shadow area.
        //
        // For class methods, `this` is param 0 at $fp+8. Load it once into a temp
        // so the body can reference it via SymbolTable.currThis.
        if (SymbolTable.getInstance().getCurrentClass() != null) {
            Temp thisTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandLoadLocal(thisTemp, 8));
            SymbolTable.getInstance().currThis = thisTemp;
        }

        if (body != null) body.irMe();

        // Always emit a fall-through return so functions without an explicit
        // return don't run off the end of their bodies into the next function.
        // For non-void this is technically UB per spec §3, but the canonical
        // behavior is to return whatever happens to be in $v0.
        Ir.getInstance().AddIrCommand(new IrCommandReturn(null, funcType));
        Ir.getInstance().AddIrCommand(new IrCommandFuncEnd(label));

        // Locals fully tallied — settle the frame size.
        funcType.frameSize = funcType.localSlotCount * 4;

        SymbolTable.getInstance().setCurrentFunction(null);
        SymbolTable.getInstance().currThis = null;
        return null;
    }
}
