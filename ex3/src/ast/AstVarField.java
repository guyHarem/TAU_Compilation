package ast;

import types.*;

public class AstVarField extends AstVar {
    public AstVar var;
    public String fieldName;

    public AstVarField(AstVar var, String fieldName, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.fieldName = fieldName;
        this.line = lineNum;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("field (%s)", fieldName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }

    @Override
    public Type semantMe() {
        // Get the type of the base variable
        Type varType = var.semantMe();

        // Check that the base is a class type
        if (!(varType instanceof TypeClass)) {
            throw new SemanticError(line, "field access on non-class type");
        }

        // Look up the field in the class hierarchy
        TypeClass classType = (TypeClass) varType;
        Type fieldType = classType.findMember(fieldName);

        if (fieldType == null) {
            throw new SemanticError(line, "undefined field: " + fieldName);
        }

        return fieldType;
    }
}
