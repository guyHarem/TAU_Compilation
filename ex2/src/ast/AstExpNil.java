package ast;

public class AstExpNil extends AstExp {
    
    public AstExpNil(int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();
    }
    
    public void PrintMe() {
        System.out.format("AST NODE EXP NIL\n");

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "EXP\nNIL"
        );
    }
}
