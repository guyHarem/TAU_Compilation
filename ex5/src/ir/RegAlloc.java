package ir;

import java.util.*;
import temp.*;
import cfg.*;

public class RegAlloc {
    public static final int K = 10; 
    private List<CFGNode> nodes;
    
    public Map<Temp, String> allocation = new HashMap<>();
    private Map<Integer, Set<Temp>> in = new HashMap<>();
    private Map<Integer, Set<Temp>> out = new HashMap<>();

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
                Set<Temp> oldOut = new HashSet<>(out.get(n.id));

                Set<Temp> currentOut = new HashSet<>();
                for (CFGNode succ : n.successors) {
                    Set<Temp> succIn = in.get(succ.id);
                    if (succIn.contains(null)) throw new RuntimeException("Null detected in IN set of node " + succ.id);
                    currentOut.addAll(succIn);
                }
                out.put(n.id, currentOut);

                Set<Temp> currentIn = new HashSet<>(currentOut);
                
                List<Temp> defs = n.command.getDefTemps();
                if (defs != null) {
                    for (Temp d : defs) {
                        if (d != null) currentIn.remove(d);
                    }
                }
                
                List<Temp> uses = n.command.getUsedTemps();
                if (uses != null) {
                    for (Temp u : uses) {
                        if (u != null) currentIn.add(u);
                    }
                }

                if (currentIn.contains(null)) throw new RuntimeException("Liveness analysis generated null Temp at node " + n.id);
                in.put(n.id, currentIn);

                if (!currentIn.equals(oldIn) || !currentOut.equals(oldOut)) changed = true;
            }
        }

        // System.out.println("\n[LIVENESS] [IN]:");
        // in.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        
        // System.out.println("\n[LIVENESS] [OUT]:");
        // out.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        // System.out.println();
    }

    private void buildInterferenceGraph() {
        allTemps.clear();
        adj.clear();
        degree.clear();
        allocation.clear();

        for (CFGNode n : nodes) {
            for (Temp t : n.command.getUsedTemps()) if (t != null) allTemps.add(t);
            for (Temp t : n.command.getDefTemps()) if (t != null) allTemps.add(t);
        }
        
        for (Temp t : allTemps) {
            adj.put(t, new HashSet<>());
            degree.put(t, 0);
        }

        for (CFGNode n : nodes) {
            List<Temp> defs = n.command.getDefTemps();
            Set<Temp> liveOut = out.get(n.id);
            if (liveOut.contains(null)) throw new RuntimeException("Null found in OUT set during graph construction at node " + n.id);

            for (Temp d : defs) {
                if (d == null) continue;
                for (Temp l : liveOut) {
                    if (n.command instanceof IrCommandMove && l.equals(((IrCommandMove) n.command).src)) continue;
                    addEdge(d, l);
                }
            }
        }
        
        // System.out.println("\n[INTERFERCE] [ADJ]:");
        // adj.forEach((id, temps) -> System.out.println("Node " + id + ": " + temps));
        // System.out.println();
    }

    private void addEdge(Temp u, Temp v) {
        if (u == null || v == null) throw new RuntimeException("Attempted to add edge with null Temp");
        if (u != v && !adj.get(u).contains(v)) {
            adj.get(u).add(v);
            adj.get(v).add(u);
            degree.put(u, degree.get(u) + 1);
            degree.put(v, degree.get(v) + 1);
        }
    }

    private void simplify() {
        List<Temp> worklist = new ArrayList<>(allTemps);
        Map<Temp, Integer> currentDegrees = new HashMap<>(degree);

        while (!worklist.isEmpty()) {
            Temp found = null;
            for (Temp t : worklist) {
                if (currentDegrees.get(t) < K) {
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
                currentDegrees.put(neighbor, currentDegrees.get(neighbor) - 1);
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

    // We also make sure that the returned list is unique.
    public List<String> getLiveRegsForCall(IrCommand cmd) {
        List<String> liveRegs = new ArrayList<>();
        CFGNode target = null;
        for (CFGNode n : nodes) {
            if (n.command == cmd) { target = n; break; }
        }
        if (target == null) return liveRegs;

        Set<Temp> liveTemps = out.get(target.id);
        for (Temp t : liveTemps) {
            String reg = allocation.get(t);
            if (reg != null && !liveRegs.contains(reg)) liveRegs.add(reg);
        }
        return liveRegs;
    }
}
