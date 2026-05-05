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

public class IrCommandStringLabel extends IrCommand
{
	public String label;
	public String value;
	
	public IrCommandStringLabel(String label, String value)
	{
		this.label = label;
		this.value = value;
	}
	
	@Override public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() { MipsGenerator.getInstance().addStringLabel(label, value); }
}
