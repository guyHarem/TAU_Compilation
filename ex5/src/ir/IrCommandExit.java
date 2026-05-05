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
    public List<Temp> getUsedTemps() {
        return Collections.emptyList();
    }

    @Override
    public List<Temp> getDefTemps() {
        return Collections.emptyList();
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().exit();
    }
}
