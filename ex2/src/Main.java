import java.io.*;
import java_cup.runtime.Symbol;
import ast.*;
public class Main
{
    static public void main(String argv[])
    {
        Lexer l;
        Parser p;
        Symbol s;
        AstProgram ast;
        FileReader fileReader;
        PrintWriter fileWriter = null;
        String inputFileName = argv[0];
        String outputFileName = argv[1];
       
        try
        {
            // Initialize fileWriter here so it can be passed to the Parser and referenced in catch
            fileReader = new FileReader(inputFileName);
            fileWriter = new PrintWriter(outputFileName);
            l = new Lexer(fileReader);
            p = new Parser(l, fileWriter);
            s = p.parse();
           
            // This code runs only if parsing completed successfully
            fileWriter.print("OK");
            fileWriter.flush();
            fileWriter.close();
           
            // This is needed for making the actual AST, but we remove it for the test.
            // ast = (AstProgram) s.value;
            // ast.printMe();
            AstGraphviz.getInstance().finalizeFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
