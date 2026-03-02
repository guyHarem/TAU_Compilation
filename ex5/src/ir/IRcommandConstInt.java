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

public class IrCommandConstInt extends IrCommand
{
	public Temp dst;
	public int value;
	
	public IrCommandConstInt(Temp dst, int value)
	{
		this.dst = dst;
		this.value = value;
	}
}
