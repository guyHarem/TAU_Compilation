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

public class IrCommandStrLen extends IrCommand
{
	private Temp str;
	private Temp dst;

	public IrCommandStrLen(Temp dst, Temp str) {
		this.str = str;
		this.dst = dst;
	}
	
	
	@Override
	public void mipsMe()
	{
		MipsGenerator.getInstance().strLen(this.dst, this.str);
	}
}
