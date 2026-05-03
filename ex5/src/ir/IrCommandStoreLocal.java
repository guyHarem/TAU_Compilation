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

public class IrCommandStoreLocal extends IrCommand
{
	public Temp var;
	public Temp val;
	
	public IrCommandStoreLocal(Temp var, Temp val)
	{
		this.val	= val;
		this.var	= var;
	}

	public void mipsMe()
	{
		MipsGenerator.getInstance().storeLocal(this.var, this.val);
	}
}
