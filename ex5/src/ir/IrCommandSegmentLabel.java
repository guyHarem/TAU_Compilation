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

public class IrCommandSegmentLabel extends IrCommand {
    public String segmentName;

    public IrCommandSegmentLabel(String segmentName) {
        this.segmentName = segmentName;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().segmentLabel(segmentName);
    }
}
