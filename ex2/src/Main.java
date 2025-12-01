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
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];
        
        try
        {
            fileReader = new FileReader(inputFileName);
            fileWriter = new PrintWriter(outputFileName);
            l = new Lexer(fileReader);
            p = new Parser(l, fileWriter);

            s = p.parse();
            fileWriter.print("OK");
            
            ast = (AstProgram) s.value;
            ast.printMe();
            
            fileWriter.flush();
            fileWriter.close();
            AstGraphviz.getInstance().finalizeFile();
        }
        catch (Exception e)
        {
            if (e.getMessage() != null && e.getMessage().equals("LEX_ERROR")) {
               try {
                   PrintWriter pw = new PrintWriter(outputFileName);
                   pw.print("ERROR");
                   pw.close();
               } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
            } else {
                e.printStackTrace();
            }
        }
    }
}
