/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

%class Lexer
%line
%column
%cupsym TokenNames
%cup

/****************/
/* DECLARATIONS */
/****************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; }

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; }

    /*******************************************/
	/* DEBUG PRINT HELPER (NEWLY ADDED) */
	/*******************************************/
	private void debug(String tokenName) {
        // Print to System.err to avoid polluting the output file
        System.err.println("LEXER DEBUG: Matched " + tokenName + " '" + yytext() + "' (Line " + getLine() + ", Col " + getTokenStartPosition() + ")");
	}
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t]
INTEGER			= 0 | [1-9][0-9]*
INVALID_NUMBER = 0[0-9]+
IDENTIFIER		= [a-zA-Z][a-zA-Z0-9]*
STRING_TEXT		= [a-zA-Z]*
STRING			= \"{STRING_TEXT}\"
DOLLAR_SIGN  	= \$

LINE_COMMENT_CHAR = [a-zA-Z0-9 \t()\[\]{}?!+\-*\/.;]
LINE_COMMENT    = "//" {LINE_COMMENT_CHAR}* {LineTerminator}

BLOCK_COMMENT_CHAR = [a-zA-Z0-9 \t\r\n()\[\]{}?!+\-*\/.;]
BLOCK_COMMENT   = "/*" {BLOCK_COMMENT_CHAR}* "*/"

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

<YYINITIAL> {

/* Keywords - must come before IDENTIFIER */
"class"				{ debug("CLASS"); return symbol(TokenNames.CLASS, "CLASS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"nil"				{ debug("NIL"); return symbol(TokenNames.NIL, "NIL[" + getLine() + "," + getTokenStartPosition() + "]"); }
"array"				{ debug("ARRAY"); return symbol(TokenNames.ARRAY, "ARRAY[" + getLine() + "," + getTokenStartPosition() + "]"); }
"while"				{ debug("WHILE"); return symbol(TokenNames.WHILE, "WHILE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"if"				{ debug("IF"); return symbol(TokenNames.IF, "IF[" + getLine() + "," + getTokenStartPosition() + "]"); }
"else"				{ debug("ELSE"); return symbol(TokenNames.ELSE, "ELSE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"new"				{ debug("NEW"); return symbol(TokenNames.NEW, "NEW[" + getLine() + "," + getTokenStartPosition() + "]"); }
"extends"			{ debug("EXTENDS"); return symbol(TokenNames.EXTENDS, "EXTENDS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"return"			{ debug("RETURN"); return symbol(TokenNames.RETURN, "RETURN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"int"				{ debug("TYPE_INT"); return symbol(TokenNames.TYPE_INT, "TYPE_INT[" + getLine() + "," + getTokenStartPosition() + "]"); }
"string"			{ debug("TYPE_STRING"); return symbol(TokenNames.TYPE_STRING, "TYPE_STRING[" + getLine() + "," + getTokenStartPosition() + "]"); }
"void"				{ debug("TYPE_VOID"); return symbol(TokenNames.TYPE_VOID, "TYPE_VOID[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Operators */
":="				{ debug("ASSIGN"); return symbol(TokenNames.ASSIGN, "ASSIGN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"="					{ debug("EQ"); return symbol(TokenNames.EQ, "EQ[" + getLine() + "," + getTokenStartPosition() + "]"); }
"<"					{ debug("LT"); return symbol(TokenNames.LT, "LT[" + getLine() + "," + getTokenStartPosition() + "]"); }
">"					{ debug("GT"); return symbol(TokenNames.GT, "GT[" + getLine() + "," + getTokenStartPosition() + "]"); }
"+"					{ debug("PLUS"); return symbol(TokenNames.PLUS, "PLUS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"-"					{ debug("MINUS"); return symbol(TokenNames.MINUS, "MINUS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"*"					{ debug("TIMES"); return symbol(TokenNames.TIMES, "TIMES[" + getLine() + "," + getTokenStartPosition() + "]"); }
"/"					{ debug("DIVIDE"); return symbol(TokenNames.DIVIDE, "DIVIDE[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Punctuation */
"("					{ debug("LPAREN"); return symbol(TokenNames.LPAREN, "LPAREN[" + getLine() + "," + getTokenStartPosition() + "]"); }
")"					{ debug("RPAREN"); return symbol(TokenNames.RPAREN, "RPAREN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"["					{ debug("LBRACK"); return symbol(TokenNames.LBRACK, "LBRACK[" + getLine() + "," + getTokenStartPosition() + "]"); }
"]"					{ debug("RBRACK"); return symbol(TokenNames.RBRACK, "RBRACK[" + getLine() + "," + getTokenStartPosition() + "]"); }
"{"					{ debug("LBRACE"); return symbol(TokenNames.LBRACE, "LBRACE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"}"					{ debug("RBRACE"); return symbol(TokenNames.RBRACE, "RBRACE[" + getLine() + "," + getTokenStartPosition() + "]"); }
","					{ debug("COMMA"); return symbol(TokenNames.COMMA, "COMMA[" + getLine() + "," + getTokenStartPosition() + "]"); }
"."					{ debug("DOT"); return symbol(TokenNames.DOT, "DOT[" + getLine() + "," + getTokenStartPosition() + "]"); }
";"					{ debug("SEMICOLON"); return symbol(TokenNames.SEMICOLON, "SEMICOLON[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Comments (Skipped tokens) */
{LINE_COMMENT}		{ debug("LINE_COMMENT (Skipped)"); /* just skip, do nothing */ }
{BLOCK_COMMENT}		{ debug("BLOCK_COMMENT (Skipped)"); /* just skip, do nothing */ }

/* Catch invalid comments (Error-throwing tokens) */
"//" { debug("🔥 INVALID COMMENT (Partial Line)"); throw new RuntimeException("LEX_ERROR"); }
"/*" { debug("🔥 INVALID COMMENT (Partial Block)"); throw new RuntimeException("LEX_ERROR"); }

/* Invalid numbers with leading zeros (Error-throwing token) */
{INVALID_NUMBER}	{ debug("🔥 INVALID_NUMBER"); throw new RuntimeException("LEX_ERROR"); }

/* Integers - validate range */
{INTEGER}			{
						try {
                            debug("INT (Pre-check)");
							int val = Integer.parseInt(yytext());
							if( val < 0 || val > 32767){
                                debug("🔥 INT (Out of Range)");
								throw new NumberFormatException("Value exceeds L language limit");
							}
							return symbol(TokenNames.INT, "INT(" + val + ")[" + getLine() + "," + getTokenStartPosition() + "]");
						} catch (NumberFormatException e) { 
							throw new RuntimeException("LEX_ERROR");
						}
}

/* Strings */
{STRING}			{ debug("STRING"); return symbol(TokenNames.STRING, "STRING(" + yytext() + ")[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Identifiers - must come after keywords */
{IDENTIFIER}		{ debug("ID"); return symbol(TokenNames.ID, "ID(" + yytext() + ")[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Whitespace (Skipped token) */
{WhiteSpace}		{ debug("WhiteSpace (Skipped)"); /* just skip what was found, do nothing */ }

/* End of file */
{DOLLAR_SIGN}		{ debug("DOLLAR_SIGN"); return symbol(TokenNames.EOF); }
<<EOF>>             { debug("EOF"); return symbol(TokenNames.EOF); }

/* Error - anything else is a lexical error (Catch-all) */
.					{ debug("🔥 CATCH-ALL (NO MATCH)"); throw new RuntimeException("LEX_ERROR"); }

}
