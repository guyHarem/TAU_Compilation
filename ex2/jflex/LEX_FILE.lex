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
"class"				{ return symbol(TokenNames.CLASS); }
"nil"				{ return symbol(TokenNames.NIL); }
"array"				{ return symbol(TokenNames.ARRAY); }
"while"				{ return symbol(TokenNames.WHILE); }
"if"				{ return symbol(TokenNames.IF); }
"else"				{ return symbol(TokenNames.ELSE); }
"new"				{ return symbol(TokenNames.NEW); }
"extends"			{ return symbol(TokenNames.EXTENDS); }
"return"			{ return symbol(TokenNames.RETURN); }
"int"				{ return symbol(TokenNames.TYPE_INT); }
"string"			{ return symbol(TokenNames.TYPE_STRING); }
"void"				{ return symbol(TokenNames.TYPE_VOID); }

/* Operators - two-character operators must come before single-character ones */
":="				{ return symbol(TokenNames.ASSIGN); }
"="					{ return symbol(TokenNames.EQ); }
"<"					{ return symbol(TokenNames.LT); }
">"					{ return symbol(TokenNames.GT); }
"+"					{ return symbol(TokenNames.PLUS); }
"-"					{ return symbol(TokenNames.MINUS); }
"*"					{ return symbol(TokenNames.TIMES); }
"/"					{ return symbol(TokenNames.DIVIDE); }

/* Punctuation */
"("					{ return symbol(TokenNames.LPAREN); }
")"					{ return symbol(TokenNames.RPAREN); }
"["					{ return symbol(TokenNames.LBRACK); }
"]"					{ return symbol(TokenNames.RBRACK); }
"{"					{ return symbol(TokenNames.LBRACE); }
"}"					{ return symbol(TokenNames.RBRACE); }
","					{ return symbol(TokenNames.COMMA); }
"."					{ return symbol(TokenNames.DOT); }
";"					{ return symbol(TokenNames.SEMICOLON); }

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
							return symbol(TokenNames.INT, Integer.valueOf(val));
						} catch (NumberFormatException e) { //Either number it too large for Java, or exeecds L language limit
							return symbol(TokenNames.ERROR, "ERROR");
						}
}

/* Strings */
{STRING}			{ return symbol(TokenNames.STRING, yytext()); }

// {INVALID_STRING}	{return symbol(TokenNames.ERROR, "ERROR");}

/* Identifiers - must come after keywords */
{IDENTIFIER}		{ return symbol(TokenNames.ID, yytext()); }

/* Whitespace */
{WhiteSpace}		{ /* just skip what was found, do nothing */ }

/* End of file */
{DOLLAR_SIGN}		{ return symbol(TokenNames.EOF); }
<<EOF>>             { return symbol(TokenNames.EOF); }

/* Error - anything else is a lexical error */
.					{ return symbol(TokenNames.ERROR, "ERROR"); }

}