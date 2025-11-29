package ast;

public class AstFuncDec extends AstDec {

    public AstType type;
    public String name;
    public AstList<AstVarDec> params;
    public AstList<AstStmt> body;

    public AstFuncDec(AstType type, String name, AstList<AstVarDec> params, AstList<AstStmt> body, int lineNum) {
        super(lineNum);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    public void PrintMe() {
        System.out.format("AST NODE FUNC DEC: %s\n", name);
        if (type != null) type.printMe();
        if (params != null) params.printMe();
        if (body != null) body.printMe();
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FUNC\nDEC\n(%s)", name));
        if (type != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (params != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
        if (body != null) 
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }
}
