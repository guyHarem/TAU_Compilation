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

public class IrCommandStrCopy extends IrCommand
{
	private Temp str;
	private Temp dst;

	public IrCommandStrCopy(Temp dst, Temp str) {
		this.str = str;
		this.dst = dst;
	}
	
	
	@Override public List<Temp> getUsedTemps() { return Arrays.asList(str, dst); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String s = RegAlloc.getInstance().allocation.get(str);
        String d = RegAlloc.getInstance().allocation.get(dst);
        MipsGenerator.getInstance().strCopy(d, s);
    }
}
