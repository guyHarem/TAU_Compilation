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

public class IrCommandStoreAt extends IrCommand {
    public Temp address;
    public Temp size;

    public IrCommandStoreAt(Temp address, Temp size) {
        this.address = address;
        this.size = size;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(address, value); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String a = RegAlloc.getInstance().allocation.get(address);
        String v = RegAlloc.getInstance().allocation.get(value);
        MipsGenerator.getInstance().storeAt(a, v);
    }
}
