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

public abstract class IrCommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int labelCounter = 0;
	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s", labelCounter++,msg);
	}

	// Anonymous Label (for the case where msg is not a valid variable name).
	public    static String getFreshAnonymousLabel()
	{
		return String.format("TempLabel_%d", labelCounter++);
	}
	
	public abstract void mipsMe();
	public abstract List<Temp> getUsedTemps();
    public abstract List<Temp> getDefTemps();
}
