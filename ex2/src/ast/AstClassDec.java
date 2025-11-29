package ast;

public class AstClassDec extends AstDec {

    public String name;
    public String parent;
    public AstList<AstDec> fields;

    public AstClassDec(String name, String parent, AstList<AstDec> fields, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.parent = parent;
        this.fields = fields;
    }
    
    public void PrintMe() {
        System.out.format("AST NODE CLASS DEC: %s EXTENDS %s\n", name, parent != null ? parent : "null");
        if (fields != null) fields.printMe();
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("CLASS\nDEC\n(%s)", name));
        if (fields != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    } 
}
