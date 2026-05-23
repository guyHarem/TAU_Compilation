package ir;

import ast.AstFuncDec;
import java.util.ArrayList;
import java.util.List;
import mips.MipsGenerator;
import temp.*;
import cfg.*;

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
        Ir.getInstance().AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_INV_PTR));
        Temp invPtrMsg = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(invPtrMsg, MipsGenerator.STRING_INV_PTR));
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(invPtrMsg));
        Ir.getInstance().AddIrCommand(new IrCommandExit());
        Ir.getInstance().AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_INV_PTR));


        // ACCESS VIOLATION: Load the address of the global string into a temp for the print command
        Ir.getInstance().AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_ACCESS_VIOLATION));
        Temp accessViolationMsg = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(accessViolationMsg, MipsGenerator.STRING_ACCESS_VIOLATION));
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(accessViolationMsg));
        Ir.getInstance().AddIrCommand(new IrCommandExit());
        Ir.getInstance().AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_ACCESS_VIOLATION));

        // ILLEGAL DIV BY ZERO
        Ir.getInstance().AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_DIV0));
        Temp div0Msg = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoadAddress(div0Msg, MipsGenerator.STRING_DIV0));
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(div0Msg));
        Ir.getInstance().AddIrCommand(new IrCommandExit());
        Ir.getInstance().AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_DIV0));
    }

    public void finalizePrintIntCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp num = tf.getFreshTemp();

        // strLen
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_PRINT_INT));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", num));
        ir.AddIrCommand(new IrCommandPrintInt(num));
        ir.AddIrCommand(new IrCommandReturn(null));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_PRINT_INT));
    }

    public void finalizePrintStringCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp str = tf.getFreshTemp();

        // strLen
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_PRINT_STRING));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", str));
        ir.AddIrCommand(new IrCommandNilCheck(str));
        ir.AddIrCommand(new IrCommandPrintString(str));
        ir.AddIrCommand(new IrCommandReturn(null));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_PRINT_STRING));
    }

    public void finalizeMallocCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), size = tf.getFreshTemp();

        // strLen
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_MALLOC));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", size));
        ir.AddIrCommand(new IrCommandMalloc(dst, size));
        ir.AddIrCommand(new IrCommandReturn(dst));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_MALLOC));
    }

    public void finalizeAllocArrayCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), len = tf.getFreshTemp();
        Temp byteSize = tf.getFreshTemp();
        Temp one = tf.getFreshTemp();
        Temp four = tf.getFreshTemp();

        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_ALLOC_ARRAY));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", len));
        // Out-of-bounds includes len <= 0 at runtime.
        ir.AddIrCommand(new IrCommandBoundsCheckLength(len));
        // byteSize = (len + 1) * 4   — slot 0 holds the length itself.
        ir.AddIrCommand(new IrCommandConstInt(one, 1));
        ir.AddIrCommand(new IrCommandConstInt(four, 4));
        ir.AddIrCommand(new IrCommandBinopAddIntegers(byteSize, len, one));
        ir.AddIrCommand(new IrCommandBinopMulIntegers(byteSize, byteSize, four));
        ir.AddIrCommand(new IrCommandCall(dst, null, MipsGenerator.LABEL_MALLOC, new Temp[]{byteSize}));
        // Store the length at dst[0] so subsequent bounds checks can read it.
        ir.AddIrCommand(new IrCommandStoreAt(dst, len));
        ir.AddIrCommand(new IrCommandReturn(dst));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_ALLOC_ARRAY));
    }

    public void finalizeStrlenCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), arg1 = tf.getFreshTemp();

        // strLen
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_STRLEN));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", arg1));
        ir.AddIrCommand(new IrCommandNilCheck(arg1));
        ir.AddIrCommand(new IrCommandStrLen(dst, arg1));
        ir.AddIrCommand(new IrCommandReturn(dst));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_STRLEN));
    }

    public void finalizeStrcpyCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), arg1 = tf.getFreshTemp();

        // strCopy
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_STRCOPY));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", dst));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a1", arg1));
        ir.AddIrCommand(new IrCommandNilCheck(dst));
        ir.AddIrCommand(new IrCommandNilCheck(arg1));
        ir.AddIrCommand(new IrCommandStrCopy(dst, arg1));
        ir.AddIrCommand(new IrCommandReturn(null));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_STRCOPY));
    }

    // Spec §2.1: `=` on two strings tests contents equality.
    public void finalizeStrEqCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), s1 = tf.getFreshTemp(), s2 = tf.getFreshTemp();

        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_STR_EQ));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", s1));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a1", s2));
        ir.AddIrCommand(new IrCommandNilCheck(s1));
        ir.AddIrCommand(new IrCommandNilCheck(s2));
        ir.AddIrCommand(new IrCommandStrEq(dst, s1, s2));
        ir.AddIrCommand(new IrCommandReturn(dst));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_STR_EQ));
    }

    // Logic inside IrCommandBinopAddStrings or the caller
    public void finalizeStringConcatCode() {
        Ir ir = Ir.getInstance();
        TempFactory tf = TempFactory.getInstance();
        Temp dst = tf.getFreshTemp(), s1 = tf.getFreshTemp(), s2 = tf.getFreshTemp();
        
        // 0. Setup dst, s1, s2.
        ir.AddIrCommand(new IrCommandFuncStart(MipsGenerator.LABEL_STR_CONCAT));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a0", s1));
        ir.AddIrCommand(new IrCommandMoveFromReg("$a1", s2));
        
        ir.AddIrCommand(new IrCommandNilCheck(s1));
        ir.AddIrCommand(new IrCommandNilCheck(s2));

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
        ir.AddIrCommand(new IrCommandCall(dst, null, MipsGenerator.LABEL_MALLOC, new Temp[]{totalSize}));
        Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_STRCOPY, new Temp[]{dst, s1}));

        // 5. Copy s2 to dst + len1
        Temp dstPlusLen1 = tf.getFreshTemp();
        ir.AddIrCommand(new IrCommandBinopAddIntegers(dstPlusLen1, dst, len1));
        Ir.getInstance().AddIrCommand(new IrCommandCall(null, null, MipsGenerator.LABEL_STRCOPY, new Temp[]{dstPlusLen1, s2}));
        
        // 6. return dst
        ir.AddIrCommand(new IrCommandReturn(dst));
        ir.AddIrCommand(new IrCommandFuncEnd(MipsGenerator.LABEL_STR_CONCAT));
    }

    /**
     * Reconstructs the IR stream in the correct MIPS execution order.
     */
    public void finalizeIr() {
        finalizePrintIntCode();
        finalizePrintStringCode();
        finalizeMallocCode();
        finalizeAllocArrayCode();
        finalizeStrlenCode();
        finalizeStrcpyCode();
        finalizeStrEqCode();
        finalizeStringConcatCode();
        finalizeErrorCode();
        List<IrCommand> masterList = new ArrayList<>();

        // 1. Data Segment
        masterList.add(new IrCommandSegmentLabel("data"));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_ACCESS_VIOLATION, "\"Access Violation\""));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_DIV0, "\"Illegal Division By Zero\""));
        masterList.add(new IrCommandStringLabel(MipsGenerator.STRING_INV_PTR, "\"Invalid Pointer Dereference\""));
        masterList.addAll(globalDecls);

        // 2. Text Segment & Entry Point.
        // SPIM enters at `main`, so the runtime block (global init + jal user main + exit)
        // is what's actually labelled `main`. The user's source-level main is renamed to
        // `_user_main` in AstFuncDec.irMe.
        masterList.add(new IrCommandSegmentLabel("text"));
        masterList.add(new IrCommandFuncEnd("initialization_section"));
        masterList.add(new IrCommandFuncStart("main"));

        // 3. Global Initializations
        masterList.addAll(globalInits);

        // 4. User Main Call & Exit
        masterList.add(new IrCommandCall(null, null, "_user_main", new Temp[]{}));
        masterList.add(new IrCommandExit());
        masterList.add(new IrCommandFuncEnd("main"));

        // 5. Everything else (functions, etc.)
        masterList.addAll(commands);

        // Replace the current command list with the finalized one
        this.commands = masterList;
    }

    public List<List<IrCommand>> splitIrByFunctions() {
        List<List<IrCommand>> functions = new ArrayList<>();
        List<IrCommand> currentFunc = new ArrayList<>();
        for (int i = 0; i < this.commands.size(); i++) {
            IrCommand current = this.commands.get(i);
            // A function always starts with its entry label
            if (current instanceof IrCommandFuncEnd) {
                functions.add(currentFunc); currentFunc = null;
                continue; // No need to add current.
            }
            if (current instanceof IrCommandFuncStart) currentFunc = new ArrayList<>();

            if (currentFunc != null) currentFunc.add(current);
        }
        if (currentFunc != null) functions.add(currentFunc);
        return functions;
    }

    public void mipsMe() {
        List<List<IrCommand>> functions = splitIrByFunctions();
        for (List<IrCommand> func : functions) {
            List<CFGNode> cfgNodes = CFGBuilder.buildCFG(func);
            CFGBuilder.printToDotFile(cfgNodes, "dbg.txt"); // TODO: rm.
            RegAlloc.setInstance(cfgNodes);
            for (IrCommand cmd : func) {
                cmd.mipsMe();
            }
        }
    }
}
