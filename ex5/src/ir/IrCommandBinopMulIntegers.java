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

public class IrCommandBinopMulIntegers extends IrCommandBinop
{
	public IrCommandBinopMulIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().mul(dst,t1,t2);
	}
}
