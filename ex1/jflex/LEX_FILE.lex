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

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; }

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; }
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t]
INTEGER			= 0 | [1-9][0-9]*
INVALID_NUMBER = 0[0-9]+ //NOT ALLOWING INT TO START WITH 0
IDENTIFIER		= [a-zA-Z][a-zA-Z0-9]*
STRING_TEXT		= [a-zA-Z]*
STRING			= \"{STRING_TEXT}\"
// INVALID_STRING	= \"[^\"]*\"  // Any string that contains any character (invalid strings will be caught)
DOLLAR_SIGN  	= \$

/* Comment characters: letters, digits, white spaces, ( ) [ ] { } ? ! + - * / . ; */

/* For line comments: everything except newlines */
LINE_COMMENT_CHAR = [a-zA-Z0-9 \t()\[\]{}?!+\-*\/.;]
LINE_COMMENT    = "//" {LINE_COMMENT_CHAR}* {LineTerminator} //ADDED LINE TERMINATOR


/* For block comments: everything including newlines */
BLOCK_COMMENT_CHAR = [a-zA-Z0-9 \t\r\n()\[\]{}?!+\-*\/.;]
BLOCK_COMMENT   = "/*" {BLOCK_COMMENT_CHAR}* "*/"

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

/* Keywords - must come before IDENTIFIER */
"class"				{ return symbol(TokenNames.CLASS, "CLASS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"nil"				{ return symbol(TokenNames.NIL, "NIL[" + getLine() + "," + getTokenStartPosition() + "]"); }
"array"				{ return symbol(TokenNames.ARRAY, "ARRAY[" + getLine() + "," + getTokenStartPosition() + "]"); }
"while"				{ return symbol(TokenNames.WHILE, "WHILE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"if"				{ return symbol(TokenNames.IF, "IF[" + getLine() + "," + getTokenStartPosition() + "]"); }
"else"				{ return symbol(TokenNames.ELSE, "ELSE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"new"				{ return symbol(TokenNames.NEW, "NEW[" + getLine() + "," + getTokenStartPosition() + "]"); }
"extends"			{ return symbol(TokenNames.EXTENDS, "EXTENDS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"return"			{ return symbol(TokenNames.RETURN, "RETURN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"int"				{ return symbol(TokenNames.TYPE_INT, "TYPE_INT[" + getLine() + "," + getTokenStartPosition() + "]"); }
"string"			{ return symbol(TokenNames.TYPE_STRING, "TYPE_STRING[" + getLine() + "," + getTokenStartPosition() + "]"); }
"void"				{ return symbol(TokenNames.TYPE_VOID, "TYPE_VOID[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Operators - two-character operators must come before single-character ones */
":="				{ return symbol(TokenNames.ASSIGN, "ASSIGN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"="					{ return symbol(TokenNames.EQ, "EQ[" + getLine() + "," + getTokenStartPosition() + "]"); }
"<"					{ return symbol(TokenNames.LT, "LT[" + getLine() + "," + getTokenStartPosition() + "]"); }
">"					{ return symbol(TokenNames.GT, "GT[" + getLine() + "," + getTokenStartPosition() + "]"); }
"+"					{ return symbol(TokenNames.PLUS, "PLUS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"-"					{ return symbol(TokenNames.MINUS, "MINUS[" + getLine() + "," + getTokenStartPosition() + "]"); }
"*"					{ return symbol(TokenNames.TIMES, "TIMES[" + getLine() + "," + getTokenStartPosition() + "]"); }
"/"					{ return symbol(TokenNames.DIVIDE, "DIVIDE[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Punctuation */
"("					{ return symbol(TokenNames.LPAREN, "LPAREN[" + getLine() + "," + getTokenStartPosition() + "]"); }
")"					{ return symbol(TokenNames.RPAREN, "RPAREN[" + getLine() + "," + getTokenStartPosition() + "]"); }
"["					{ return symbol(TokenNames.LBRACK, "LBRACK[" + getLine() + "," + getTokenStartPosition() + "]"); }
"]"					{ return symbol(TokenNames.RBRACK, "RBRACK[" + getLine() + "," + getTokenStartPosition() + "]"); }
"{"					{ return symbol(TokenNames.LBRACE, "LBRACE[" + getLine() + "," + getTokenStartPosition() + "]"); }
"}"					{ return symbol(TokenNames.RBRACE, "RBRACE[" + getLine() + "," + getTokenStartPosition() + "]"); }
","					{ return symbol(TokenNames.COMMA, "COMMA[" + getLine() + "," + getTokenStartPosition() + "]"); }
"."					{ return symbol(TokenNames.DOT, "DOT[" + getLine() + "," + getTokenStartPosition() + "]"); }
";"					{ return symbol(TokenNames.SEMICOLON, "SEMICOLON[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Comments */
{LINE_COMMENT}		{ /* just skip, do nothing */ }
{BLOCK_COMMENT}		{ /* just skip, do nothing */ }


/* Invalid numbers with leading zeros - MUST COME BEFORE INTEGER!*/
{INVALID_NUMBER}	{return symbol(TokenNames.ERROR, "ERROR");}

/* Integers - validate range */
{INTEGER}			{
						try {
							int val = Integer.parseInt(yytext());
							if( val < 0 || val > 32767){
								throw new NumberFormatException("Value exceeds L language limit");
							}
							return symbol(TokenNames.INT, "INT(" + val + ")[" + getLine() + "," + getTokenStartPosition() + "]");
						} catch (NumberFormatException e) { //Either number it too large for Java, or exeecds L language limit
							return symbol(TokenNames.ERROR, "ERROR");
						}
}

/* Strings */
{STRING}			{ return symbol(TokenNames.STRING, "STRING(" + yytext() + ")[" + getLine() + "," + getTokenStartPosition() + "]"); }

// {INVALID_STRING}	{return symbol(TokenNames.ERROR, "ERROR");}

/* Identifiers - must come after keywords */
{IDENTIFIER}		{ return symbol(TokenNames.ID, "ID(" + yytext() + ")[" + getLine() + "," + getTokenStartPosition() + "]"); }

/* Whitespace */
{WhiteSpace}		{ /* just skip what was found, do nothing */ }

/* End of file */
{DOLLAR_SIGN}		{ return symbol(TokenNames.EOF); }
<<EOF>>             { return symbol(TokenNames.EOF); }

/* Error - anything else is a lexical error */
.					{ return symbol(TokenNames.ERROR, "ERROR"); }

}