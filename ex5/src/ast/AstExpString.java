package ast;

import ir.*;
import temp.*;
import types.*;

public class AstExpString extends AstExp
{
	public String value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpString(String value, int lineNum)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.value = value;
		this.line = lineNum;
	}

	/******************************************************/
	/* The printing message for a STRING EXP AST node */
	/******************************************************/
	public void printMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST STRING EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",value);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("STRING\n%s",value.replace('"','\'')));
	}

	public Type semantMe()
	{
		return TypeString.getInstance();
	}

	@Override
	public Temp irMe() {
		System.out.println("Debug: IR Gen for " + this.getClass().getSimpleName());
		String label = IrCommand.getFreshAnonymousLabel();

		// Add the string to the global data collection
		Ir.getInstance().AddIrGlobalDecleration(new IrCommandStringLabel(label, this.value));
		Temp dst = TempFactory.getInstance().getFreshTemp();
		Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(dst, label));
		return dst;
	}
}
