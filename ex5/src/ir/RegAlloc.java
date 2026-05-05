package ir;

import java.util.*;
import temp.*;
import cfg.*;

public class RegAlloc {
    public static final int K = 10; 
    private List<CFGNode> nodes; // Now operating on the CFG
    
    public Map<Temp, String> allocation = new HashMap<>();
    private Map<Integer, Set<Temp>> in = new HashMap<>();
    private Map<Integer, Set<Temp>> out = new HashMap<>();

    // Graph Structures
    private Map<Temp, Set<Temp>> adj = new HashMap<>();
    private Map<Temp, Integer> degree = new HashMap<>();
    private Stack<Temp> stack = new Stack<>();
    private Set<Temp> allTemps = new HashSet<>();

    private static RegAlloc instance = null;
    public static RegAlloc getInstance() { return instance; }
    
    public static void setInstance(List<CFGNode> commands) {
        instance = new RegAlloc(commands);
        instance.run();
    }

    private RegAlloc(List<CFGNode> nodes) {
        this.nodes = nodes;
    }

    private void run() {
        computeLiveness();
        buildInterferenceGraph();
        simplify();
        assignColors();
    }

    private void computeLiveness() {
        for (CFGNode n : nodes) {
            in.put(n.id, new HashSet<>());
            out.put(n.id, new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = nodes.size() - 1; i >= 0; i--) {
                CFGNode n = nodes.get(i);
                Set<Temp> oldIn = new HashSet<>(in.get(n.id));

                Set<Temp> currentOut = out.get(n.id);
                for (CFGNode succ : n.successors) {
                    currentOut.addAll(in.get(succ.id));
                }

                Set<Temp> currentIn = in.get(n.id);
                currentIn.clear();
                currentIn.addAll(currentOut);
                currentIn.removeAll(n.command.getDefTemps());
                currentIn.addAll(n.command.getUsedTemps());

                if (!currentIn.equals(oldIn)) changed = true;
            }
        }

        // Print liveness results
        System.out.println("\n[LIVENESS] [IN]:");
        in.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        
        System.out.println("\n[LIVENESS] [OUT]:");
        out.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        System.out.println();
    }

    private void buildInterferenceGraph() {
        // 1. Initialize nodes
        for (CFGNode n : nodes) {
            allTemps.addAll(n.command.getUsedTemps());
            allTemps.addAll(n.command.getDefTemps());
        }
        for (Temp t : allTemps) {
            adj.put(t, new HashSet<>());
            degree.put(t, 0);
        }

        // 2. Correct Interference Logic
        for (CFGNode n : nodes) {
            List<Temp> defs = n.command.getDefTemps();
            Set<Temp> liveOut = out.get(n.id);
            for (Temp d : defs) {
                for (Temp l : liveOut) {
                    // Destination does NOT interfere with Source in a MOVE (skip 'd = l' cases).
                    if (n.command instanceof IrCommandMove && l.equals(((IrCommandMove) n.command).src)) continue;
                    addEdge(d, l);
                }
            }
        }
        
        System.out.println("\n[INTERFERCE] [ADJ]:");
        adj.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        System.out.println();
    }

    private void addEdge(Temp u, Temp v) {
        if (u != v && !adj.get(u).contains(v)) {
            adj.get(u).add(v);
            adj.get(v).add(u);
            degree.put(u, degree.get(u) + 1);
            degree.put(v, degree.get(v) + 1);
        }
    }

    private void simplify() {
        List<Temp> worklist = new ArrayList<>(allTemps);
        while (!worklist.isEmpty()) {
            Temp found = null;
            // Chaitin's heuristic: find node with degree < K
            for (Temp t : worklist) {
                if (degree.get(t) < K) {
                    found = t;
                    break;
                }
            }

            if (found == null) {
                System.out.println("Register Allocation Failed: Spilling required.");
                System.exit(0);
            }

            stack.push(found);
            worklist.remove(found);
            for (Temp neighbor : adj.get(found)) {
                degree.put(neighbor, degree.get(neighbor) - 1);
            }
        }
    }

    private void assignColors() {
        while (!stack.isEmpty()) {
            Temp t = stack.pop();
            Set<String> usedColors = new HashSet<>();
            for (Temp neighbor : adj.get(t)) {
                String color = allocation.get(neighbor);
                if (color != null) usedColors.add(color);
            }

            for (int i = 0; i < K; i++) {
                String color = "$t" + i;
                if (!usedColors.contains(color)) {
                    allocation.put(t, color);
                    break;
                }
            }
        }
    }

    public List<String> getLiveRegsForCall(IrCommand cmd) {
        List<String> liveRegs = new ArrayList<>();
        // Find the node corresponding to this command
        CFGNode target = null;
        for (CFGNode n : nodes) {
            if (n.command == cmd) { target = n; break; }
        }
        if (target == null) return liveRegs;

        Set<Temp> liveTemps = out.get(target.id);
        for (Temp t : liveTemps) {
            String reg = allocation.get(t);
            if (reg != null) liveRegs.add(reg);
        }
        return liveRegs;
    }
}
