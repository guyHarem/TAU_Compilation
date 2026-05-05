/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import mips.*;
import temp.*;

public class IrCommandJumpIfEqToZero extends IrCommand
{
	public Temp t;
	public String labelName;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		this.t          = t;
		this.labelName = labelName;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        return Arrays.asList(t);
    }

    @Override
    public List<Temp> getDefTemps() {
        return Collections.emptyList();
    }

    @Override
    public void mipsMe()
    {
        String r = RegAlloc.getInstance().allocation.get(t);
        MipsGenerator.getInstance().beqz(r, labelName);
    }
}
