package ast;

public class AstType extends AstNode {

    public String name;
     
    public AstType(String name, int lineNum){
        super(lineNum);
        this.name = name;
    }

    public void PrintMe() {
        System.out.format("AST TYPE: %s\n", name);
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("TYPE\n(%s)", name));
    }
}
