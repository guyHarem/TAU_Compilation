package ast;

public class AstClassDec extends AstDec {
    public String name;
    public String parent;
    public AstList<AstDec> fields;

    public AstClassDec(String name, String parent, AstList<AstDec> fields, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.parent = parent;
        this.fields = fields;
    }

    public void printMe() {
        String parentStr = (parent != null) ? parent : "null";
        String output = String.format("def class (%s)\nextends (%s)", name, parentStr);
        if (fields != null) fields.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, output);
        if (fields != null) AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }
}
