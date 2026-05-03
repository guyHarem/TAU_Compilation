package ir;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Reconstructs the IR stream in the correct MIPS execution order.
     */
    public void finalizeIr() {
        List<IrCommand> masterList = new ArrayList<>();

        // 1. Data Segment
        masterList.add(new IrCommandLabel(".data"));
        masterList.addAll(globalDecls);

        // 2. Text Segment & Entry Point
        masterList.add(new IrCommandLabel(".text"));
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
        for (IrCommand cmd : commands) {
            cmd.mipsMe();
        }
    }
}
