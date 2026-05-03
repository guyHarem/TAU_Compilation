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

    public void mipsMe() {
        MipsGenerator.getInstance().storeAt(address, size);
    }
}
