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
import temp.*;
import mips.*;

public class IrCommandMove extends IrCommand {
    public Temp src;
    public Temp dst;

    public IrCommandMove(Temp src, Temp dst) {
        this.src = src;
        this.dst = dst;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().moveLocal(src, dst);
    }
}
