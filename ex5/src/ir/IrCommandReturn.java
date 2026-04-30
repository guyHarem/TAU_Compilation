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

// TODO: Actually add return value in excercise 5. Also add conversion to mips.
public class IrCommandReturn extends IrCommand
{
	public Temp retval;
	
	public IrCommandReturn(Temp retval)
	{
		this.retval = retval;
	}

	public void mipsMe()
	{
		// TODO.
	}
}
