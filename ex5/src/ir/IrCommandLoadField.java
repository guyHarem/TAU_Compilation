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
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().loadField(this.dst, this.thisPtr, this.fieldOffset);
	}
}
