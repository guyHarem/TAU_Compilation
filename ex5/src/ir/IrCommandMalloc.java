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
    public int size;

    public IrCommandMalloc(Temp dst, int size) {
        this.dst = dst;
        this.size = size;
    }

    public void mipsMe() {
        MipsGenerator.getInstance().malloc(dst, size);
    }
}
