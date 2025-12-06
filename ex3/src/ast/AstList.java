package ast;

public class AstList<T extends AstNode> extends AstNode {
    public T head;
    public AstList<T> tail;
    private final String typeName;

    public AstList(T head, AstList<T> tail, String typeName, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;
        this.typeName = typeName;
    }

    @Override
    public void printMe() {
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, typeName + " list");
        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
