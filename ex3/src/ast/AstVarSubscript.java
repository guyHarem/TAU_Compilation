package ast;

import types.*;

public class AstVarSubscript extends AstVar {
    public AstVar var;
    public AstExp subscript;

    public AstVarSubscript(AstVar var, AstExp subscript, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.subscript = subscript;
        this.line = lineNum;
    }

	@Override
    public void printMe() {
        if (var != null) var.printMe();
        if (subscript != null) subscript.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "subscript var");
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
    }

    @Override
    public Type semantMe() {
        // Get the type of the base variable
        Type varType = var.semantMe();

        // Check that the base is an array type
        if (!(varType instanceof TypeArray)) {
            throw new SemanticError(line, "subscript on non-array type");
        }

        // Check that the subscript expression is int
        Type indexType = subscript.semantMe();
        if (indexType != TypeInt.getInstance()) {
            throw new SemanticError(line, "array subscript must be int");
        }

        // Check for negative constant subscript (including expressions like 0-1)
        Integer constantIndex = subscript.evaluateConstant();
        if (constantIndex != null && constantIndex < 0) {
            throw new SemanticError(subscript.line, "array subscript cannot be negative");
        }

        // Return the element type of the array
        return ((TypeArray) varType).elementType;
    }
}
