import ir.*;
import java.io.PrintWriter;
import java.util.*;

public class UninitializedAnalyzer {
    private Map<Integer, Set<String>> in = new HashMap<>();
    private Map<Integer, Set<String>> out = new HashMap<>();
    private Set<String> errors = new TreeSet<>(); // TreeSet for sorted unique names 

    public void analyze(List<CFGNode> nodes, PrintWriter writer) {
        for (CFGNode node : nodes) {
            in.put(node.id, new HashSet<>());
            out.put(node.id, new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (CFGNode node : nodes) {
                Set<String> oldOut = new HashSet<>(out.get(node.id));

                // IN[n] = Intersection of OUT of all predecessors (Must-Analysis) [cite: 35]
                if (!node.predecessors.isEmpty()) {
                    Set<String> intersection = null;
                    for (CFGNode pred : node.predecessors) {
                        if (intersection == null) {
                            intersection = new HashSet<>(out.get(pred.id));
                        } else {
                            intersection.retainAll(out.get(pred.id));
                        }
                    }
                    in.put(node.id, intersection != null ? intersection : new HashSet<>());
                }

                // Compute OUT[n] via Transfer Function [cite: 32, 36]
                Set<String> resultOut = transfer(node, in.get(node.id));
                out.put(node.id, resultOut);

                if (!resultOut.equals(oldOut)) changed = true;
            }
        }

        // Output results [cite: 68, 69, 70, 71]
        if (errors.isEmpty()) {
            writer.println("OK");
        } else {
            for (String varName : errors) {
                writer.println(varName);
            }
        }
    }
    
    private Set<String> transfer(CFGNode node, Set<String> currentIn) {
        Set<String> currentOut = new HashSet<>(currentIn);
        IrCommand cmd = node.command;

        // Rule: Variable use (Load)
        if (cmd instanceof IrCommandLoad) {
            IrCommandLoad load = (IrCommandLoad) cmd; // Manual cast
            String uniqueKey = load.varName;
            
            if (!currentIn.contains(uniqueKey)) {
                errors.add(uniqueKey.split("@")[0]);
            } else {
                currentOut.add(load.dst.toString());
            }
        }

        // Rule: Assignment (Store)
        else if (cmd instanceof IrCommandStore) {
            IrCommandStore store = (IrCommandStore) cmd; // Manual cast
            String uniqueKey = store.varName;
            String srcTemp = store.src.toString();

            if (currentIn.contains(srcTemp)) {
                currentOut.add(uniqueKey);
            } else {
                currentOut.remove(uniqueKey);
            }
        }

        // Rule: Binops propagate uninitialized status
        else if (cmd instanceof IrCommandBinop) {
            IrCommandBinop bin = (IrCommandBinop) cmd; // Manual cast
            if (currentIn.contains(bin.t1.toString()) && currentIn.contains(bin.t2.toString())) {
                currentOut.add(bin.dst.toString());
            } else {
                currentOut.remove(bin.dst.toString());
            }
        }

        // Rule: Constants are always initialized
        else if (cmd instanceof IrCommandConstInt) {
            IrCommandConstInt constInt = (IrCommandConstInt) cmd; // Manual cast
            currentOut.add(constInt.dst.toString());
        }
        
        return currentOut;
    }
}
