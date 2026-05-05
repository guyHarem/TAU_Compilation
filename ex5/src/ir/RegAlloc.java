package ir;

import java.util.*;
import temp.*;

public class RegAlloc {
    public static final int K = 10; 
    private List<IrCommand> commands;
    
    // The final mapping: Temp ID -> Physical Register String
    public Map<Temp, String> allocation = new HashMap<>();
    
    // Liveness info for caller-save logic
    private Map<IrCommand, Set<Temp>> liveOutMap = new HashMap<>();

    // Internal Graph Structures
    private Map<Temp, Set<Temp>> adj = new HashMap<>();
    private Map<Temp, Integer> degree = new HashMap<>();
    private Stack<Temp> stack = new Stack<>();
    private Set<Temp> allTemps = new HashSet<>();

    private static RegAlloc instance = null;
    public static RegAlloc getInstance() { return instance; }
    
    public static void setInstance(List<IrCommand> commands) {
        instance = new RegAlloc(commands);
        instance.run();
    }

    private RegAlloc(List<IrCommand> commands) {
        this.commands = commands;
    }

    private void run() {
        computeLiveness();
        buildInterferenceGraph();
        simplify();
        assignColors();
    }

    private void computeLiveness() {
        Set<Temp> live = new HashSet<>();
        for (int i = commands.size() - 1; i >= 0; i--) {
            IrCommand cmd = commands.get(i);
            liveOutMap.put(cmd, new HashSet<>(live));
            live.removeAll(cmd.getDefTemps());
            live.addAll(cmd.getUsedTemps());
        }
    }

    /**
     * Helper for IrCommandCall: returns physical registers ($t0-$t9) 
     * that are live immediately after this command.
     */
    public List<String> getLiveRegsForCall(IrCommand cmd) {
        List<String> liveRegs = new ArrayList<>();
        Set<Temp> liveTemps = liveOutMap.get(cmd);
        if (liveTemps == null) return liveRegs;

        for (Temp t : liveTemps) {
            String reg = allocation.get(t);
            if (reg != null) liveRegs.add(reg);
        }
        return liveRegs;
    }

    private void buildInterferenceGraph() {
        // Collect all temps
        for (IrCommand cmd : commands) {
            allTemps.addAll(cmd.getUsedTemps());
            allTemps.addAll(cmd.getDefTemps());
        }
        for (Temp t : allTemps) {
            adj.put(t, new HashSet<>());
            degree.put(t, 0);
        }

        // Build edges: A variable is live-out of a command, it interferes with what that command defines
        for (IrCommand cmd : commands) {
            Set<Temp> liveOut = liveOutMap.get(cmd);
            for (Temp def : cmd.getDefTemps()) {
                for (Temp l : liveOut) {
                    addEdge(def, l);
                }
            }
        }
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
            for (Temp t : worklist) {
                if (degree.get(t) < K) {
                    found = t;
                    break;
                }
            }

            if (found == null) {
                System.out.println("Register Allocation Failed");
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
}
