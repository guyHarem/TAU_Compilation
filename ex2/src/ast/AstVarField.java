package ast;

public class AstVarField extends AstVar {
    public AstVar var;
    public String fieldName;

    public AstVarField(AstVar var, String fieldName, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.fieldName = fieldName;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("field (%s)", fieldName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }
}
