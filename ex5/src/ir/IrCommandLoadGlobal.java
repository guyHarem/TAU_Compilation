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

public class IrCommandLoadGlobal extends IrCommand
{
	public Temp dst;
	public String varName;
	
	public IrCommandLoadGlobal(Temp dst, String varName)
	{
		this.dst      = dst;
		this.varName = varName;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().loadGlobal(dst, varName);
	}
}
