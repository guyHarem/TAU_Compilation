package ast;

/**
 * Exception thrown when a semantic error is detected.
 * Contains the line number where the error occurred.
 */
public class SemanticError extends RuntimeException {
    public int line;

    public SemanticError(int line, String message) {
        super(message);
        this.line = line;
    }

    public SemanticError(int line) {
        super("Semantic error at line " + line);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
