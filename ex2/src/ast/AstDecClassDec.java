package ast;

public class AstDecClassDec extends AstDec {

    public AstClassDec dec;

    public AstDecClassDec(AstClassDec dec ,int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.dec = dec;
    }
    
    public void PrintMe()
    {
        System.out.print("UNKNOWN AST CLASS DECLARATION NODE");

        if (dec != null) dec.PrintMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "DEC\nCLASS DEC");

        if (dec != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }
    
}
