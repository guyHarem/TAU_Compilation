import ir.*;
import java.io.*;
import java.util.*;

public class CFGBuilder {
    public static List<CFGNode> buildCFG() {
        List<CFGNode> nodeList = new ArrayList<>();
        Map<String, CFGNode> labelMap = new HashMap<>();

        // Phase 1: Convert IR linked list to a flat ArrayList of CFGNodes
        Ir irInstance = Ir.getInstance();
        IrCommand currentCmd = irInstance.getHead();
        
        // Use a simple list-based traversal. 
        // If your IR uses IrCommandList, we extract the head and move to the tail's head.
        IrCommandList currentList = irInstance.getTailList();

        while (currentCmd != null) {
            CFGNode node = new CFGNode(currentCmd);
            nodeList.add(node);
            
            // Map labels to nodes for Phase 2
            if (currentCmd instanceof IrCommandLabel) {
                labelMap.put(((IrCommandLabel) currentCmd).labelName, node);
            }

            // Correct traversal: Extract the next command from the list structure
            if (currentList != null) {
                currentCmd = currentList.head;
                currentList = currentList.tail;
            } else currentCmd = null;
        }
		System.out.println("[DEBUG] NodeList length: " + nodeList.size());

        // Phase 2: Connect the edges based on Command types
        for (int i = 0; i < nodeList.size(); i++) {
		    System.out.println("[DEBUG] CFGBuilder " + i + ": " + nodeList.get(i));
            CFGNode node = nodeList.get(i);
            IrCommand cmd = node.command;

            if (cmd instanceof IrCommandJumpLabel) {
                String target = ((IrCommandJumpLabel) cmd).labelName;
                CFGNode targetNode = labelMap.get(target);
                if (targetNode != null) {
                    node.successors.add(targetNode);
                }
                // Unconditional jumps DO NOT fall through to i + 1
            } 
            else if (cmd instanceof IrCommandJumpIfEqToZero) {
                String target = ((IrCommandJumpIfEqToZero) cmd).labelName;
                CFGNode targetNode = labelMap.get(target);
                if (targetNode != null) {
                    node.successors.add(targetNode);
                }
                // Conditional jumps DO fall through to the next instruction
                if (i + 1 < nodeList.size()) {
                    node.successors.add(nodeList.get(i + 1));
                }
            } 
            else {
                // Sequential flow for all other commands (Store, Load, Binop, etc.)
                if (i + 1 < nodeList.size()) {
                    node.successors.add(nodeList.get(i + 1));
                }
            }
        }
        return nodeList;
    }

    public static void printToDotFile(List<CFGNode> nodes, String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("digraph CFG {");
            for (CFGNode node : nodes) {
                String cleanLabel = node.getLabel().replace("\"", "\\\"");
                writer.format("    %s (%d);\n", cleanLabel, node.id);

                for (CFGNode successor : node.successors) {
                    if (successor != null) {
                        writer.format("        %d -> %d;\n", node.id, successor.id);
                    }
                }
            }
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
