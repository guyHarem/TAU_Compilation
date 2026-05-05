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

public class IrCommandLoadAddress extends IrCommand
{
	public Temp dst;
	public String label;
	
	public IrCommandLoadAddress(Temp dst, String label)
	{
		this.dst = dst;
		this.label = label;
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
        String d = RegAlloc.getInstance().allocation.get(this.dst);
        MipsGenerator.getInstance().loadAddress(d, this.label);
    }
}
