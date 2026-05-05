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

public class IrCommandStrLen extends IrCommand
{
	private Temp str;
	private Temp dst;

	public IrCommandStrLen(Temp dst, Temp str) {
		this.str = str;
		this.dst = dst;
	}
	
	
	@Override public List<Temp> getUsedTemps() { return Arrays.asList(str); }
    @Override public List<Temp> getDefTemps() { return Arrays.asList(dst); }
    @Override public void mipsMe() {
        String s = RegAlloc.getInstance().allocation.get(str);
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().strLen(d, s);
    }
}
