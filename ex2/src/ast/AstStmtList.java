package ast;

public class AstStmtList extends AstNode {

    public AstStmt head;
    public AstStmtList tail;

    /*******************/
    /*  CONSTRUCTOR(S) */
    /*******************/
    public AstStmtList(int lineNum, AstStmt head, AstStmtList tail)
    {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;
    }
    
    public void printMe()
    {
        System.out.print("AST NODE STMT LIST\n");

        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "STMT LIST\n");

        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);

    }
}
