package ir;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import mips.*;
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

	@Override
	public List<Temp> getUsedTemps() {
		return Collections.emptyList();
	}

	@Override
	public List<Temp> getDefTemps() {
		return Arrays.asList(dst);
	}

	@Override
	public void mipsMe()
	{
		String d = RegAlloc.getInstance().allocation.get(dst);
		MipsGenerator.getInstance().li(d, value);
	}
}
