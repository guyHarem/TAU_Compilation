package ast;

public class AstList<T extends AstNode> extends AstNode {
    public T head;
    public AstList<T> tail;
    
    // We store the type name (e.g., "stmt", "dec") to generate labels
    private String typeName; 

    public AstList(T head, AstList<T> tail, String typeName) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;
        this.typeName = typeName;
        String plural = typeName + "s"; // e.g. "stmt" -> "stmts"
        if (tail != null) System.out.print("====================== " + plural + " -> " + typeName + " " + plural + "\n");
        else System.out.print("====================== " + plural + " -> " + typeName + "      \n");
    }

    @Override
    public void printMe() {
        // e.g. "AST NODE STMT LIST"
        System.out.print("AST NODE " + typeName.toUpperCase() + " LIST\n");
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        // GraphViz Label: e.g. "STMT\nLIST"
        AstGraphviz.getInstance().logNode(
            serialNumber,
            typeName.toUpperCase() + "\nLIST\n"
        );
        
        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}