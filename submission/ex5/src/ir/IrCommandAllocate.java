/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.Collections;
import java.util.List;
import mips.*;
import temp.*;

public class IrCommandAllocate extends IrCommand
{
	public String varName;
	
	public IrCommandAllocate(String varName)
	{
		this.varName = varName;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        return Collections.emptyList();
    }

    @Override
    public List<Temp> getDefTemps() {
        return Collections.emptyList();
    }
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().allocate(varName);
	}
}
