package ir;

import mips.*;
import types.TypeFunction;

// Function entry. For user functions emits a full prologue:
//   subu $sp, $sp, total
//   sw   $ra, (total-4)($sp)
//   sw   $fp, (total-8)($sp)
//   addu $fp, $sp, total-8
//   sw   $a0..$a3 to $fp+8..+20  (only paramCount slots)
// Runtime helpers pass funcType=null and skip the prologue.
public class IrCommandFuncStart extends IrCommand
{
	public String labelName;
	public TypeFunction funcType;

	public IrCommandFuncStart(String labelName) { this.labelName = labelName; }
	public IrCommandFuncStart(String labelName, TypeFunction funcType) {
		this.labelName = labelName;
		this.funcType = funcType;
	}

	@Override public java.util.List<temp.Temp> getUsedTemps() { return java.util.Collections.emptyList(); }
	@Override public java.util.List<temp.Temp> getDefTemps()  { return java.util.Collections.emptyList(); }

	@Override
	public void mipsMe() {
		MipsGenerator g = MipsGenerator.getInstance();
		g.label(labelName);
		if (funcType == null) return;

		int total = funcType.frameSize + 8; // locals + saved $ra/$fp
		g.prologue(total, funcType.paramCount);
	}
}