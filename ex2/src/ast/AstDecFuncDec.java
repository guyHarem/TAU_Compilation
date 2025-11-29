package ast;

public class AstDecFuncDec extends AstDec {

    public final AstFuncDec dec;

    public AstDecFuncDec(AstFuncDec dec, int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.dec = dec;
    }

    public void PrintMe() {
        System.out.print("AST NODE VAR DEC\n");
        if (dec != null) dec.PrintMe();
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "DEC\nVAR DEC");
        if (dec != null)
        AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }
}
