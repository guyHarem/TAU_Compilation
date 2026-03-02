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

public class IrCommandBinopSubIntegers extends IrCommandBinop
{
	public IrCommandBinopSubIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().sub(dst, t1, t2);
	}
}
