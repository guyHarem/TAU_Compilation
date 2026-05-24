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

public class IrCommandStoreGlobal extends IrCommand
{
	public String varName;
	public Temp src;
	
	public IrCommandStoreGlobal(String varName, Temp src)
	{
		this.src      = src;
		this.varName = varName;
	}

	@Override public List<Temp> getUsedTemps() { return Arrays.asList(src); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String s = RegAlloc.getInstance().allocation.get(src);
        MipsGenerator.getInstance().storeGlobal(varName, s);
    }
}
