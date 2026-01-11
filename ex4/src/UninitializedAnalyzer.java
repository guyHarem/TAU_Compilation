import ir.*;
import java.io.PrintWriter;
import java.util.*;

public class UninitializedAnalyzer {
    private Map<Integer, Set<String>> in = new HashMap<>();
    private Map<Integer, Set<String>> out = new HashMap<>();
    private Set<String> errors = new TreeSet<>();

    public void analyze(List<CFGNode> nodes, PrintWriter writer) {
        if (nodes.isEmpty()) return;

        // 1. Universal Set: All possible name@offset variables
        Set<String> allVars = new HashSet<>();
        for (CFGNode n : nodes) {
            if (n.command instanceof IrCommandLoad) allVars.add(((IrCommandLoad)n.command).varName);
            if (n.command instanceof IrCommandStore) allVars.add(((IrCommandStore)n.command).varName);
            if (n.command instanceof IrCommandAllocate) allVars.add(((IrCommandAllocate)n.command).varName);
        }

        // 2. Initialization: Internal nodes start at TOP, Entry starts at BOTTOM
        for (CFGNode node : nodes) {
            // We set everything to TOP initially to allow intersection to work 
            in.put(node.id, new HashSet<>(allVars));
            out.put(node.id, new HashSet<>(allVars));
        }

        // CRITICAL: Node 0 (The absolute start) must be EMPTY [cite: 152, 161]
        CFGNode entry = nodes.get(0);
        in.put(entry.id, new HashSet<>()); 
        out.put(entry.id, transfer(entry, in.get(entry.id))); // Calculate actual OUT for entry

        // 3. Worklist: Start ONLY with Node 0's successors to force the "empty" flow
        Queue<CFGNode> worklist = new LinkedList<>();
        worklist.addAll(entry.successors);

        while (!worklist.isEmpty()) {
            CFGNode node = worklist.poll();
            Set<String> oldOut = new HashSet<>(out.get(node.id));

            // IN[n] = Intersection of all predecessors [cite: 128, 132]
            if (!node.predecessors.isEmpty()) {
                Set<String> intersection = null;
                for (CFGNode pred : node.predecessors) {
                    if (intersection == null) {
                        intersection = new HashSet<>(out.get(pred.id));
                    } else {
                        intersection.retainAll(out.get(pred.id)); // MUST Analysis [cite: 132]
                    }
                }
                in.put(node.id, intersection != null ? intersection : new HashSet<>());
            }

            // OUT[n] = transfer(IN[n]) [cite: 140, 145]
            Set<String> resultOut = transfer(node, in.get(node.id));
            out.put(node.id, resultOut);

            if (!resultOut.equals(oldOut)) {
                for (CFGNode succ : node.successors) {
                    if (!worklist.contains(succ)) worklist.add(succ);
                }
            }
        }

        // 4. Output results
        if (errors.isEmpty()) writer.println("OK");
        else for (String var : errors) writer.println(var);
    }

    private Set<String> transfer(CFGNode node, Set<String> currentIn) {
        Set<String> currentOut = new HashSet<>(currentIn);
        IrCommand cmd = node.command;

        if (cmd instanceof IrCommandLoad) {
            IrCommandLoad load = (IrCommandLoad) cmd;
            if (!currentIn.contains(load.varName)) errors.add(load.varName.split("@")[0]);
            else currentOut.add(load.dst.toString());
        } else if (cmd instanceof IrCommandStore) {
            IrCommandStore store = (IrCommandStore) cmd;
            if (currentIn.contains(store.src.toString())) currentOut.add(store.varName);
            else currentOut.remove(store.varName);
        } else if (cmd instanceof IrCommandBinop) {
            IrCommandBinop bin = (IrCommandBinop) cmd;
            if (currentIn.contains(bin.t1.toString()) && currentIn.contains(bin.t2.toString()))
                currentOut.add(bin.dst.toString());
            else currentOut.remove(bin.dst.toString());
        } else if (cmd instanceof IrCommandConstInt) {
            currentOut.add(((IrCommandConstInt)cmd).dst.toString());
        }
        return currentOut;
    }
}
