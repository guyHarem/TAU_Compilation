package ir;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import mips.*;
import temp.*;
import types.TypeFunction;

public class IrCommandReturn extends IrCommand
{
	public Temp retval;
	public TypeFunction funcType; // null for runtime helpers

	public IrCommandReturn(Temp retval) { this.retval = retval; }
	public IrCommandReturn(Temp retval, TypeFunction funcType) {
		this.retval = retval;
		this.funcType = funcType;
	}

	@Override public List<Temp> getUsedTemps() { return retval != null ? Arrays.asList(retval) : Collections.emptyList(); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        MipsGenerator g = MipsGenerator.getInstance();
        if (retval != null) {
            String r = RegAlloc.getInstance().allocation.get(retval);
            g.moveToReg("$v0", r);
        }
        if (funcType != null) {
            g.epilogue();
        } else {
            g.ret();
        }
    }
}
