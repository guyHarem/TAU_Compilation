/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import mips.*;
import temp.*;

public class IrCommandStoreAt extends IrCommand {
    public Temp address;
    public Temp value;

    public IrCommandStoreAt(Temp address, Temp value) {
        this.address = address;
        this.value = value;
    }

    @Override public List<Temp> getUsedTemps() { return Arrays.asList(address, value); }
    @Override public List<Temp> getDefTemps() { return Collections.emptyList(); }
    @Override public void mipsMe() {
        String a = RegAlloc.getInstance().allocation.get(address);
        String v = RegAlloc.getInstance().allocation.get(value);
        MipsGenerator.getInstance().storeAt(a, v);
    }
}
