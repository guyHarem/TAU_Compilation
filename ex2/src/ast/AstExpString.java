package ast;

public class AstExpString extends AstExp {

    public String s;
    
    public AstExpString(String s, int lineNum) {
        super(lineNum);

        serialNumber = AstNodeSerialNumber.getFresh();

        this.s = s;
    }

    public void PrintMe() {
        System.out.format("AST NODE EXP STRING(%s)\n", s);

        AstGraphviz.getInstance().logNode(
            serialNumber,
            String.format("EXP\nSTRING\n(%s)", s));
    }
    
}
