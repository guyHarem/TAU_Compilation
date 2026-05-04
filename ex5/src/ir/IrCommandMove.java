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

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().moveLocal(dst, src);
    }
}
