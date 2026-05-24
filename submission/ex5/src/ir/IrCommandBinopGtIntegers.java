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

public class IrCommandBinopGtIntegers extends IrCommandBinop
{
	public IrCommandBinopGtIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	@Override
	public void mipsMe()
    {
        String d = RegAlloc.getInstance().allocation.get(dst);
        String s1 = RegAlloc.getInstance().allocation.get(t1);
        String s2 = RegAlloc.getInstance().allocation.get(t2);

        String labelEnd        = getFreshLabel("end");
        String labelAssignOne  = getFreshLabel("AssignOne");
        String labelAssignZero = getFreshLabel("AssignZero");
        
        MipsGenerator.getInstance().blt(s2, s1, labelAssignOne);
        MipsGenerator.getInstance().bge(s2, s1, labelAssignZero);

        MipsGenerator.getInstance().label(labelAssignOne);
        MipsGenerator.getInstance().li(d, 1);
        MipsGenerator.getInstance().jump(labelEnd);

        MipsGenerator.getInstance().label(labelAssignZero);
        MipsGenerator.getInstance().li(d, 0);
        MipsGenerator.getInstance().jump(labelEnd);

        MipsGenerator.getInstance().label(labelEnd);
    }
}
