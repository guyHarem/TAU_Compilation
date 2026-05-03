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
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().storeField(this.src, this.thisPtr, this.fieldOffset);
	}
}
