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

public class IrCommandLoadGlobal extends IrCommand
{
	public Temp dst;
	public String varName;
	
	public IrCommandLoadGlobal(Temp dst, String varName)
	{
		this.dst      = dst;
		this.varName = varName;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        return Collections.emptyList();
    }

    @Override
    public List<Temp> getDefTemps() {
        return Arrays.asList(dst);
    }

    @Override
    public void mipsMe()
    {
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().loadGlobal(d, varName);
    }
}
