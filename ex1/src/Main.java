import java.io.*;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;
   
public class Main
{
	/**
     * Writes a single "ERROR" token to the output file and exits the program.
     * This function is called when a lexical error is encountered or when
     * there are remaining characters in the input file after EOF token.
     * 
     * @param outputFileName the path to the output file to be overwritten
     */
	private static void exitError(String outputFileName){
		try {
		PrintWriter fileWriter = new PrintWriter(outputFileName);
		fileWriter.print("ERROR");
		fileWriter.close();
		System.exit(1); //NEEDED????
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(1); //NEEDED?
	}
}

	static public void main(String argv[])
	{
		Lexer l;
		Symbol s;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			fileWriter = new PrintWriter(outputFileName);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);

			/***********************/
			/* [4] Read next token */
			/***********************/
			s = l.next_token();

			/********************************/
			/* [5] Main reading tokens loop */
			/********************************/
			boolean firstToken = true;
			while (s.sym != TokenNames.EOF)
			{
				/****************************************/
                /* [5.1] Check if we encountered ERROR */
                /****************************************/
                if (s.sym == TokenNames.ERROR) {
                    fileWriter.close();
                    exitError(outputFileName);
                }

				/************************/
				/* [6] Print to console */
				/************************/
				System.out.print("[");
				System.out.print(l.getLine());
				System.out.print(",");
				System.out.print(l.getTokenStartPosition());
				System.out.print("]:");
				System.out.print(s.value);
				System.out.print("\n");
				
				/*********************/
				/* [7] Print to file */
				/*********************/
				// fileWriter.print(l.getLine()); //REMOVED DUE TO OUTPUT FORMAT
				// fileWriter.print(": "); //REMOVED DUE TO OUTPUT FORMAT
				if( !firstToken) {
					fileWriter.print("\n");
				}
				fileWriter.print(s.value);
				firstToken = false;
				
				/***********************/
				/* [8] Read next token */
				/***********************/
				s = l.next_token();
			}

            /********************************************************/
            /* [9] Check if there are remaining characters in file */
            /********************************************************/
            // Try to read one more token after EOF
            s = l.next_token();
            if (s.sym != TokenNames.EOF) {
                // There's still content after the $ marker - this is an error
                fileWriter.close();
                exitError(outputFileName);
            }
            
            /******************************/
            /* [10] Close lexer input file */
            /******************************/
            l.yyclose();

            /**************************/
            /* [11] Close output file */
            /**************************/
            fileWriter.close();
        }
                 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}


