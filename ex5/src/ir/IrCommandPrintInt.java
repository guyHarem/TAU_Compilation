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

public class IrCommandPrintInt extends IrCommand
{
	public Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().printInt(t);
	}
}

