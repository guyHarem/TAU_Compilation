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

public class IrCommandBinopGtIntegers extends IrCommandBinop
{
	public IrCommandBinopGtIntegers(Temp dst, Temp t1, Temp t2) { super(dst, t1, t2); }
	
	public void mipsMe()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String labelEnd        = getFreshLabel("end");
		String labelAssignOne  = getFreshLabel("AssignOne");
		String labelAssignZero = getFreshLabel("AssignZero");
		
		/******************************************/
		/* [2] if (t2< t1) goto labelAssignOne;  */
		/*     if (t2>=t1) goto labelAssignZero; */
		/******************************************/
		MipsGenerator.getInstance().blt(t2,t1,labelAssignOne);
		MipsGenerator.getInstance().bge(t2,t1,labelAssignZero);

		/************************/
		/* [3] labelAssignOne: */
		/*                      */
		/*         t3 := 1      */
		/*         goto end;    */
		/*                      */
		/************************/
		MipsGenerator.getInstance().label(labelAssignOne);
		MipsGenerator.getInstance().li(dst,1);
		MipsGenerator.getInstance().jump(labelEnd);

		/*************************/
		/* [4] labelAssignZero: */
		/*                       */
		/*         t3 := 1       */
		/*         goto end;     */
		/*                       */
		/*************************/
		MipsGenerator.getInstance().label(labelAssignZero);
		MipsGenerator.getInstance().li(dst,0);
		MipsGenerator.getInstance().jump(labelEnd);

		/******************/
		/* [5] labelEnd: */
		/******************/
		MipsGenerator.getInstance().label(labelEnd);
	}
}
