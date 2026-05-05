/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import mips.*;
import temp.*;

public class IrCommandStoreField extends IrCommand
{
	public Temp src;
	public Temp thisPtr;
    public int fieldOffset;
	
	public IrCommandStoreField(Temp src, Temp thisPtr, int fieldOffset)
	{
		this.src      = src;
		this.thisPtr = thisPtr;
        this.fieldOffset = fieldOffset;
	}
	
	@Override public List<Temp> getUsedTemps() { return Arrays.asList(src, thisPtr); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String s = RegAlloc.getInstance().allocation.get(src);
        String p = RegAlloc.getInstance().allocation.get(thisPtr);
        MipsGenerator.getInstance().storeField(s, p, fieldOffset);
    }
}
