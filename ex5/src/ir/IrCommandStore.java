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

public class IrCommandStore extends IrCommand
{
	public String varName;
	public Temp src;
	
	public IrCommandStore(String varName, Temp src)
	{
		this.src      = src;
		this.varName = varName;
	}

	public void mipsMe()
	{
		MipsGenerator.getInstance().store(varName,src);
	}
}
