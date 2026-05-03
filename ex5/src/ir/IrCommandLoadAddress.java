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

public class IrCommandLoadAddress extends IrCommand
{
	public Temp dst;
	public String label;
	
	public IrCommandLoadAddress(Temp dst, String label)
	{
		this.dst = dst;
		this.label = label;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().loadAddress(this.dst, this.label);
	}
}
