import ir.*;
import java.io.*;
import java.util.*;

public class CFGBuilder {
    /**
     * Builds a CFG from a specific list of commands (e.g., a single function).
     */
    public static List<CFGNode> buildCFG(List<IrCommand> commands) {
        List<CFGNode> nodeList = new ArrayList<>();
        Map<String, CFGNode> labelMap = new HashMap<>();

        // Phase 1: Convert IR commands to CFGNodes and map labels
        for (IrCommand cmd : commands) {
            CFGNode node = new CFGNode(cmd);
            nodeList.add(node);
            
            if (cmd instanceof IrCommandLabel) {
                labelMap.put(((IrCommandLabel) cmd).labelName, node);
            }
        }

        // Phase 2: Connect edges based on control flow
        for (int i = 0; i < nodeList.size(); i++) {
            CFGNode node = nodeList.get(i);
            IrCommand cmd = node.command;
            boolean canMoveToNext = true;

            // 1. Unconditional Jumps
            if (cmd instanceof IrCommandJumpLabel) {
                connectToLabel(node, labelMap, ((IrCommandJumpLabel) cmd).labelName);
                canMoveToNext = false;
            } 
            // 2. Conditional Jumps (Branches)
            else if (cmd instanceof IrCommandJumpIfEqToZero) {
                connectToLabel(node, labelMap, ((IrCommandJumpIfEqToZero) cmd).labelName);
                // canMoveToNext remains true because we might NOT jump
            } 
            // 3. Program/Function Terminations
            else if (cmd instanceof IrCommandReturn || cmd instanceof IrCommandExit) {
                // Exit and Return have no successors in the local CFG
                canMoveToNext = false;
            }

            // Fall-through connection: to the physically next command
            if (canMoveToNext && (i + 1 < nodeList.size())) {
                CFGNode nextNode = nodeList.get(i + 1);
                node.successors.add(nextNode);
                nextNode.predecessors.add(node);
            }
        }
        return nodeList;
    }

    private static void connectToLabel(CFGNode source, Map<String, CFGNode> labelMap, String targetLabel) {
        CFGNode targetNode = labelMap.get(targetLabel);
        if (targetNode != null) {
            source.successors.add(targetNode);
            targetNode.predecessors.add(source);
        }
    }

    public static void printToDotFile(List<CFGNode> nodes, String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("digraph CFG {");
            for (CFGNode node : nodes) {
                String cleanLabel = node.getLabel().replace("\"", "\\\"");
                writer.format("    %d [label=\"%s (%d)\"];\n", node.id, cleanLabel, node.id);

                for (CFGNode successor : node.successors) {
                    writer.format("    %d -> %d;\n", node.id, successor.id);
                }
            }
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
