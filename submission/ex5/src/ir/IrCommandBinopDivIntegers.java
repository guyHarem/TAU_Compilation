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
		String d = RegAlloc.getInstance().allocation.get(dst);
        String s1 = RegAlloc.getInstance().allocation.get(t1);
        String s2 = RegAlloc.getInstance().allocation.get(t2);
        MipsGenerator.getInstance().div(d, s1, s2);
	}
}
