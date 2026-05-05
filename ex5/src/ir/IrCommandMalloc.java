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

public class IrCommandMalloc extends IrCommand {
    public Temp dst;
    public Temp size;

    public IrCommandMalloc(Temp dst, Temp size) {
        this.dst = dst;
        this.size = size;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(size); }
    @Override public List<Temp> getDefTemps() { return Arrays.asList(dst); }
    @Override public void mipsMe() {
        String d = RegAlloc.getInstance().allocation.get(dst);
        String s = RegAlloc.getInstance().allocation.get(size);
        MipsGenerator.getInstance().malloc(d, s);
    }
}
