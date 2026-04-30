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

public class IrCommandLabel extends IrCommand
{
	public String labelName;
	
	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
	}
	
	@Override
	public void mipsMe()
	{
		System.out.println("[CODGEN] IrCommandLabel mipsMe: " + labelName);
		MipsGenerator.getInstance().label(labelName);
	}
}
