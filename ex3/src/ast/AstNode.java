package ast;

public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;
	
	public static PrintWriter fileWriter;
	public int line;

	public AstNode(int lineNum){
		line = lineNum;
	}

	public void printError() {
		fileWriter.write("ERROR("+line+")\n");
		fileWriter.close();
		System.exit(0);
	}
	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
}
