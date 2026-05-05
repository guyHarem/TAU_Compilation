package ir;

import java.util.ArrayList;
import java.util.List;
import mips.MipsGenerator;
import temp.*;

public class Ir {
    public List<IrCommand> globalDecls = new ArrayList<>();
    public List<IrCommand> globalInits = new ArrayList<>();
    public List<IrCommand> commands = new ArrayList<>();

	public List<IrCommand> activeList = commands;

    private static Ir instance = null;
    protected Ir() {}

    public static Ir getInstance() {
        if (instance == null) instance = new Ir();
        return instance;
    }

    public void AddIrGlobalDecleration(IrCommand cmd)  { globalDecls.add(cmd); }
    public void AddIrGlobalInitialization(IrCommand cmd) { globalInits.add(cmd); }
    public void AddIrCommand(IrCommand cmd)            { activeList.add(cmd); }

    private void finalizeErrorCode() {
        // INVALID PTR: Load the address of the global string into a temp for the print command
        Ir.getInstance().AddIrCommand(new IrCommandLabel(MipsGenerator.LABEL_INV_PTR));
        Temp invPtrMsg = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(invPtrMsg, MipsGenerator.STRING_INV_PTR));
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(invPtrMsg));
        Ir.getInstance().AddIrCommand(new IrCommandExit());


        // ACCESS VIOLATION: Load the address of the global string into a temp for the print command
        Ir.getInstance().AddIrCommand(new IrCommandLabel(MipsGenerator.LABEL_ACCESS_VIOLATION));
        Temp accessViolationMsg = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(accessViolationMsg, MipsGenerator.STRING_ACCESS_VIOLATION));
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(accessViolationMsg));
        Ir.getInstance().AddIrCommand(new IrCommandExit());
    }

    public void finalizeStringUtilsCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), arg1 = tf.getFreshTemp();

        // strLen
        ir.AddIrCommand(new IrCommandLabel(MipsGenerator.LABEL_STRLEN));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", arg1));
        ir.AddIrCommand(new IrCommandStrLen(dst, arg1));
        ir.AddIrCommand(new IrCommandReturn(dst));

        // strCopy
        ir.AddIrCommand(new IrCommandLabel(MipsGenerator.LABEL_STRCOPY));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", dst));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a1", arg1));
        ir.AddIrCommand(new IrCommandStrCopy(dst, arg1));
        ir.AddIrCommand(new IrCommandReturn(dst));
    }

    // Logic inside IrCommandBinopAddStrings or the caller
    public void finalizeStringConcatCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), s1 = tf.getFreshTemp(), s2 = tf.getFreshTemp();
        
        // 0. Setup dst, s1, s2.
        ir.AddIrCommand(new IrCommandLabel(MipsGenerator.LABEL_STR_CONCAT));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", s1));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a1", s2));

        // 1. Calculate length of s1
        Temp len1 = tf.getFreshTemp();
        ir.AddIrCommand(new IrCommandConstInt(len1, 0));
        Ir.getInstance().AddIrCommand(new IrCommandCall(len1, null, MipsGenerator.LABEL_STRLEN, new Temp[]{s1}));

        // 2. Calculate length of s2
        Temp len2 = tf.getFreshTemp();
        ir.AddIrCommand(new IrCommandConstInt(len2, 0));
        Ir.getInstance().AddIrCommand(new IrCommandCall(len2, null, MipsGenerator.LABEL_STRLEN, new Temp[]{s2}));

        // 3. Malloc total size: len1 + len2 + 1
        Temp totalSize = tf.getFreshTemp();
        Temp one = tf.getFreshTemp();
        ir.AddIrCommand(new IrCommandConstInt(one, 1));
        ir.AddIrCommand(new IrCommandBinopAddIntegers(totalSize, len1, len2));
        ir.AddIrCommand(new IrCommandBinopAddIntegers(totalSize, totalSize, one));
        
        // 4. Remap s1 to larger space.
        ir.AddIrCommand(new IrCommandMalloc(dst, totalSize));
        Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_STRCOPY, new Temp[]{dst, s1}));

        // 5. Copy s2 to dst + len1
        Temp dstPlusLen1 = tf.getFreshTemp();
        ir.AddIrCommand(new IrCommandBinopAddIntegers(dstPlusLen1, dst, len1));
        Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_STRCOPY, new Temp[]{dstPlusLen1, s2}));
        
        // 6. return dst
        ir.AddIrCommand(new IrCommandReturn(dst));
    }

    /**
     * Reconstructs the IR stream in the correct MIPS execution order.
     */
    public void finalizeIr() {
        finalizeStringUtilsCode();
        finalizeStringConcatCode();
        finalizeErrorCode();
        List<IrCommand> masterList = new ArrayList<>();

        // 1. Data Segment
        masterList.add(new IrCommandSegmentLabel("data"));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_ACCESS_VIOLATION, "\"Access Violation\""));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_DIV0, "\"Illegal Division By Zero\""));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_INV_PTR, "\"Invalid Pointer Dereference\""));
        masterList.addAll(globalDecls);

        // 2. Text Segment & Entry Point
        masterList.add(new IrCommandSegmentLabel("text"));
        masterList.add(new IrCommandLabel("_start"));
        
        // 3. Global Initializations
        masterList.addAll(globalInits);

        // 4. Main Call & Exit
        masterList.add(new IrCommandCall(null, null, "main", new Temp[]{}));
        masterList.add(new IrCommandExit());

        // 5. Everything else (functions, etc.)
        masterList.addAll(commands);

        // Replace the current command list with the finalized one
        this.commands = masterList;
    }

    public void mipsMe() {
        RegAlloc.setInstance(commands);
        for (IrCommand cmd : commands) {
            cmd.mipsMe();
        }
    }
}
