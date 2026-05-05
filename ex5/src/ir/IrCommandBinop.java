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
import temp.*;

public abstract class IrCommandBinop extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	
	public IrCommandBinop(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        // Binary operations read from two source temporaries
        return Arrays.asList(t1, t2);
    }

    @Override
    public List<Temp> getDefTemps() {
        // Binary operations write the result into the destination temporary
        return Arrays.asList(dst);
    }
}
