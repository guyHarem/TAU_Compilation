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

public class IrCommandBinopEqIntegers extends IrCommandBinop
{
	public IrCommandBinopEqIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	@Override
	public void mipsMe()
    {
        String d = RegAlloc.getInstance().allocation.get(dst);
        String s1 = RegAlloc.getInstance().allocation.get(t1);
        String s2 = RegAlloc.getInstance().allocation.get(t2);

        String labelEnd        = getFreshLabel("end");
        String labelAssignOne  = getFreshLabel("AssignOne");
        String labelAssignZero = getFreshLabel("AssignZero");
        
        MipsGenerator.getInstance().beq(s1, s2, labelAssignOne);
        MipsGenerator.getInstance().bne(s1, s2, labelAssignZero);

        MipsGenerator.getInstance().label(labelAssignOne);
        MipsGenerator.getInstance().li(d, 1);
        MipsGenerator.getInstance().jump(labelEnd);

        MipsGenerator.getInstance().label(labelAssignZero);
        MipsGenerator.getInstance().li(d, 0);
        MipsGenerator.getInstance().jump(labelEnd);

        MipsGenerator.getInstance().label(labelEnd);
    }
}
