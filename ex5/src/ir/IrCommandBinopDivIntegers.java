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

public class IrCommandBinopDivIntegers extends IrCommandBinop
{
	public IrCommandBinopDivIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().div(dst,t1,t2);
	}
}
