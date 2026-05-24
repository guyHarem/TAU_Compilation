import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import mips.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AstProgram ast;
		FileReader fileReader;
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		// Set output path before MipsGenerator's singleton is touched.
		MipsGenerator.outputPath = outputFileName;

		try {
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);

			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
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

			/**********************/
			/* [8] Ir the AST ... */
			/**********************/
			ast.irMe();
			Ir.getInstance().finalizeIr();

			/***********************/
			/* [9] MIPS the Ir ... */
			/***********************/
			Ir.getInstance().mipsMe();

			/**************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AstGraphviz.getInstance().finalizeFile();

			/***************************/
			/* [11] Finalize MIPS file */
			/***************************/
			MipsGenerator.getInstance().finalizeFile();
		}

		catch (Exception e)
		{
			// e.printStackTrace();try {
			try {
				PrintWriter writer = new PrintWriter(outputFileName);
				
				if (e instanceof SemanticError) {
					// Note: Make sure your SemanticError.java class has a public 'line' field
					writer.print("ERROR(" + ((SemanticError)e).line + ")");
				} else if (e.getMessage() != null && e.getMessage().startsWith("ERROR")) {
					writer.print(e.getMessage());
				} else {
					writer.print("ERROR");
				}
				
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}