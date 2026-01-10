package ast;

import symboltable.*;
import types.*;

public class AstExpVar extends AstExp
{
    public String name;

    public AstExpVar(String name, int lineNum)
    {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.line = lineNum;
    }

    public Type semantMe()
    {
        // Use your symbol table from Ex3 to find the type and 
        // importantly, the scope/offset of this variable[cite: 286, 287].
        return SymbolTable.getInstance().find(name);
    }
}
