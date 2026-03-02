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

public class IrCommandAllocate extends IrCommand
{
	public String varName;
	
	public IrCommandAllocate(String varName)
	{
		this.varName = varName;
	}
	
	public void mipsMe()
	{
		MipsGenerator.getInstance().allocate(varName);
	}
}
