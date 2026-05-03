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

public class IrCommandStrCopy extends IrCommand
{
	private Temp str;
	private Temp dst;

	public IrCommandStrCopy(Temp dst, Temp str) {
		this.str = str;
		this.dst = dst;
	}
	
	
	@Override
	public void mipsMe()
	{
		MipsGenerator.getInstance().strCopy(this.dst, this.str);
	}
}
