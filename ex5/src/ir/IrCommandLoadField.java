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

public class IrCommandLoadField extends IrCommand
{
	public Temp thisPtr;
	public Temp dst;
	public String varName;
    public int fieldOffset;
	
	public IrCommandLoadField(Temp dst, Temp thisPtr, int fieldOffset)
	{
		this.dst      = dst;
		this.thisPtr = thisPtr;
        this.fieldOffset = fieldOffset;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        return Arrays.asList(thisPtr);
    }

    @Override
    public List<Temp> getDefTemps() {
        return Arrays.asList(dst);
    }

    @Override
    public void mipsMe()
    {
        String d = RegAlloc.getInstance().allocation.get(this.dst);
        String p = RegAlloc.getInstance().allocation.get(this.thisPtr);
        MipsGenerator.getInstance().loadField(d, p, this.fieldOffset);
    }
}
