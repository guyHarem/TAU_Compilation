package ast;

public class AstArrayTypeDef extends AstDec {

    public String name;
    public AstType type;

    public AstArrayTypeDef(String name, AstType type, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.type = type;
    }
    
    public void PrintMe() {
        System.out.format("AST NODE ARRAY TYPE DEF( %s )\n", name);

        if (type != null) type.PrintMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("ARRAY\nTYPE DEF\n(%s)", name));

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }  
}
