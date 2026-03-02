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

public class IrCommandBinopAddIntegers extends IrCommandBinop
{
	public IrCommandBinopAddIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().add(dst,t1,t2);
	}
}
