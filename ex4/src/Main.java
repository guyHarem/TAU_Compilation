import java.io.*;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import java.util.List;

public class Main {
    public static void main(String argv[]) {
        String inputFileName = argv[0];
        String outputFileName = argv[1];

        try (FileReader fileReader = new FileReader(inputFileName);
             PrintWriter fileWriter = new PrintWriter(outputFileName)) {
            
            // 1. Parse the input program
            Lexer l = new Lexer(fileReader);
            Parser p = new Parser(l);
            AstProgram ast = (AstProgram) p.parse().value;

            // 2. Semantic Analysis (sets up Symbol Table and offsets)
            ast.semantMe();

            // 3. IR Generation (Phase 1)
            ast.irMe();

            // 4. CFG Construction (Phase 2)
            List<CFGNode> cfg = CFGBuilder.buildCFG();

            // 5. Uninitialized Variable Analysis (Phase 3)
            UninitializedAnalyzer analyzer = new UninitializedAnalyzer();
            analyzer.analyze(cfg, fileWriter);

            // Cleanup Graphviz if used
            AstGraphviz.getInstance().finalizeFile();

        } catch (Exception e) {
            // Requirement 2.1: Programs have no lexical/syntax/semantic errors [cite: 67]
            // If an error occurs, it's likely an implementation bug.
            e.printStackTrace();
        }
    }
}
