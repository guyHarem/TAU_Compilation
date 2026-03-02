/***********/
/* PACKAGE */
/***********/
package symboltable;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import types.*;

public class SymbolTableEntry {
    public int index;
    public String name;
    public Type type;
    public SymbolTableEntry prevtop;
    public SymbolTableEntry next;
    public int prevtopIndex;
    public int scopeLevel;

    public SymbolTableEntry(
        String name,
        Type type,
        int index,
        SymbolTableEntry next,
        SymbolTableEntry prevtop,
        int prevtopIndex,
        int scopeLevel)
    {
        this.index = index;
        this.name = name;
        this.type = type;
        this.next = next;
        this.prevtop = prevtop;
        this.prevtopIndex = prevtopIndex;
        this.scopeLevel = scopeLevel;
    }
}
