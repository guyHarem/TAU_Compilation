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

public class IrCommandExit extends IrCommand {
    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().exit();
    }
}
