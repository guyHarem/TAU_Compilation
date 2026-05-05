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
		String d = RegAlloc.getInstance().allocation.get(dst);
        String s1 = RegAlloc.getInstance().allocation.get(t1);
        String s2 = RegAlloc.getInstance().allocation.get(t2);
		MipsGenerator.getInstance().mul(d, s1, s2);
	}
}
