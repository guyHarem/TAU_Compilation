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

public class IrCommandStringLabel extends IrCommand
{
	public String label;
	public String value;
	
	public IrCommandStringLabel(String label, String value)
	{
		this.label = label;
		this.value = value;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().addStringLabel(label, value);
	}
}
