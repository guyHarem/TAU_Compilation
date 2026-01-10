import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import java.util.List;

public class Main
{
    static public void main(String argv[])
    {
        Lexer l;
        Parser p;
        AstProgram ast;
        FileReader fileReader;
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];

        try
        {
            fileReader = new FileReader(inputFileName);
            fileWriter = new PrintWriter(outputFileName);
            l = new Lexer(fileReader);
            p = new Parser(l);

            /***********************************/
            /* [5] 3 ... 2 ... 1 ... Parse !!! */
            /***********************************/
            ast = (AstProgram) p.parse().value;

            /*************************/
            /* [6] Print the AST ... */
            /*************************/
            ast.printMe();

            /**************************/
            /* [7] Semant the AST ... */
            /**************************/
            ast.semantMe();

            /***********************************************************/
            /* [8] IR the AST: Phase 1 of Exercise 4                   */
            /***********************************************************/
            ast.irMe(); // [cite: 234]

			/***********************************************************/
			/* [9] Construct the CFG: Phase 2 of Exercise 4            */
			/***********************************************************/
			List<CFGNode> cfg = CFGBuilder.buildCFG();

			/***********************************************************/
			/* [9.1] Debug Print: Generate the CFG DOT file            */
			/***********************************************************/
			// Call the static method you implemented in CFGBuilder
			CFGBuilder.printToDotFile(cfg, "cfg.dot");

            /***********************************************************/
            /* [10] Analysis (Step 3) and Output Generation            */
            /***********************************************************/
            // Currently writing "OK" to satisfy the basic exercise flow.
            // This is where you will implement your chaotic iterations.
            fileWriter.println("OK"); // [cite: 296]

            /**************************/
            /* [11] Cleanup           */
            /**************************/
            fileWriter.close();
            AstGraphviz.getInstance().finalizeFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
