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

// TODO: Actually add return value in excercise 5. Also add conversion to mips.
public class IrCommandReturn extends IrCommand
{
	public Temp retval;
	
	public IrCommandReturn(Temp retval)
	{
		this.retval = retval;
	}

	@Override public List<Temp> getUsedTemps() { return retval != null ? Arrays.asList(retval) : Collections.emptyList(); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        if (retval != null) {
            String r = RegAlloc.getInstance().allocation.get(retval);
            MipsGenerator.getInstance().moveToReg("$v0", r);
        }
        MipsGenerator.getInstance().ret();
    }
}
