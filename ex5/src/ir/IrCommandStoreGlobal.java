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

public class IrCommandStoreGlobal extends IrCommand
{
	public String varName;
	public Temp src;
	
	public IrCommandStoreGlobal(String varName, Temp src)
	{
		this.src      = src;
		this.varName = varName;
	}

	public void mipsMe()
	{
		MipsGenerator.getInstance().storeGlobal(varName,src);
	}
}
