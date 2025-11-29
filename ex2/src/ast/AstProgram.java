package ast;

public class AstProgram extends AstNode { 

    public AstList<AstDec> decList;

    public AstProgram(AstList<AstDec> decList, int lineNum) {
        super(lineNum);
        this.decList = decList;
    }
    

    public void printMe() {
        System.out.print("AST NODE PROGRAM\n");

        if (decList != null) decList.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "PROGRAM");

        if (decList != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, decList.serialNumber);
    }
}
