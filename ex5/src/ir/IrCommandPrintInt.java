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

public class IrCommandPrintInt extends IrCommand
{
	public Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}
	
	@Override public List<Temp> getUsedTemps() { return Arrays.asList(t); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String r = RegAlloc.getInstance().allocation.get(t);
        MipsGenerator.getInstance().printInt(r);
    }
}

