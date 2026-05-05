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

public class IrCommandJumpLabel extends IrCommand
{
	public String labelName;
	
	public IrCommandJumpLabel(String labelName)
	{
		this.labelName = labelName;
	}
	
	@Override
    public List<Temp> getUsedTemps() {
        return Collections.emptyList();
    }

    @Override
    public List<Temp> getDefTemps() {
        return Collections.emptyList();
    }

    @Override
    public void mipsMe()
    {
        MipsGenerator.getInstance().jump(labelName);
    }
}
