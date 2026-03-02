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
import temp.*;
import mips.*;

public class IrCommandConstInt extends IrCommand
{
	public Temp dst;
	public int value;
	
	public IrCommandConstInt(Temp dst, int value)
	{
		this.dst = dst;
		this.value = value;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().li(t,value);
	}
}
