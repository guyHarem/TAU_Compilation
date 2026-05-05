import ir.*;
import java.util.*;

public class CFGNode {
    // Static counter to give every node a unique numerical ID
    private static int serialCounter = 0;
    
    public int id; // The field your error is complaining about
    public IrCommand command;
    public List<CFGNode> predecessors = new ArrayList<>();
    public List<CFGNode> successors = new ArrayList<>();

    public CFGNode(IrCommand command) {
        this.id = serialCounter++; // Assign and increment
        this.command = command;
    }

    public String getLabel() {
        // Returns the class name (e.g., IrCommandLoad -> Load)
        return command.getClass().getSimpleName().replace("IrCommand", "");
    }
}
