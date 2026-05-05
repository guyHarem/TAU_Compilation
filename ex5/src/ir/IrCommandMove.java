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

public class IrCommandMove extends IrCommand {
    public Temp dst;
    public Temp src;

    public IrCommandMove(Temp dst, Temp src) {
        this.dst = dst;
        this.src = src;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(src); }
    @Override public List<Temp> getDefTemps() { return Arrays.asList(dst); }
    @Override public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        String s = RegAlloc.getInstance().allocation.get(src);
        MipsGenerator.getInstance().moveLocal(d, s);
    }
}
