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
    private Symbol symbol(int type)               { return new Symbol(type, yyline, yycolumn); }
    private Symbol symbol(int type, Object value) { return new Symbol(type, yyline, yycolumn, value); }

    /*******************************************/
    /* Enable line number extraction from main */
    /*******************************************/
    public int getLine() { return yyline + 1; }

    /**********************************************/
    /* Enable token position extraction from main */
    /**********************************************/
    public int getTokenStartPosition() { return yycolumn + 1; }

    /*******************************************/
    /* DEBUG PRINT HELPER                      */
    /*******************************************/
    private void debug(String tokenName) {
        System.err.println("LEXER DEBUG: Matched " + tokenName + " '" + yytext() +
                           "' (Line " + getLine() + ", Col " + getTokenStartPosition() + ")");
    }
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t]
INTEGER        = 0 | [1-9][0-9]*
INVALID_NUMBER = 0[0-9]+
IDENTIFIER     = [a-zA-Z][a-zA-Z0-9]*
STRING_TEXT    = [a-zA-Z]*
STRING         = \"{STRING_TEXT}\"
DOLLAR_SIGN    = \$
LINE_COMMENT_CHAR  = [a-zA-Z0-9 \t()\[\]{}?!+\-*\/.;]
LINE_COMMENT       = "//" {LINE_COMMENT_CHAR}* {LineTerminator}
BLOCK_COMMENT_CHAR = [a-zA-Z0-9 \t\r\n()\[\]{}?!+\-*\/.;]
BLOCK_COMMENT      = "/*" {BLOCK_COMMENT_CHAR}* "*/"

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/
%%

<YYINITIAL> {

    /* Keywords - return only the token type */
    "class"    { debug("CLASS");    return symbol(TokenNames.CLASS); }
    "nil"      { debug("NIL");      return symbol(TokenNames.NIL); }
    "array"    { debug("ARRAY");    return symbol(TokenNames.ARRAY); }
    "while"    { debug("WHILE");    return symbol(TokenNames.WHILE); }
    "if"       { debug("IF");       return symbol(TokenNames.IF); }
    "else"     { debug("ELSE");     return symbol(TokenNames.ELSE); }
    "new"      { debug("NEW");      return symbol(TokenNames.NEW); }
    "extends"  { debug("EXTENDS");  return symbol(TokenNames.EXTENDS); }
    "return"   { debug("RETURN");   return symbol(TokenNames.RETURN); }
    "int"      { debug("TYPE_INT"); return symbol(TokenNames.TYPE_INT); }
    "string"   { debug("TYPE_STRING"); return symbol(TokenNames.TYPE_STRING); }
    "void"     { debug("TYPE_VOID");   return symbol(TokenNames.TYPE_VOID); }

    /* Operators */
    ":=" { debug("ASSIGN");  return symbol(TokenNames.ASSIGN); }
    "="  { debug("EQ");      return symbol(TokenNames.EQ); }
    "<"  { debug("LT");      return symbol(TokenNames.LT); }
    ">"  { debug("GT");      return symbol(TokenNames.GT); }
    "+"  { debug("PLUS");    return symbol(TokenNames.PLUS); }
    "-"  { debug("MINUS");   return symbol(TokenNames.MINUS); }
    "*"  { debug("TIMES");   return symbol(TokenNames.TIMES); }
    "/"  { debug("DIVIDE");  return symbol(TokenNames.DIVIDE); }

    /* Punctuation */
    "("  { debug("LPAREN");    return symbol(TokenNames.LPAREN); }
    ")"  { debug("RPAREN");    return symbol(TokenNames.RPAREN); }
    "["  { debug("LBRACK");    return symbol(TokenNames.LBRACK); }
    "]"  { debug("RBRACK");    return symbol(TokenNames.RBRACK); }
    "{"  { debug("LBRACE");    return symbol(TokenNames.LBRACE); }
    "}"  { debug("RBRACE");    return symbol(TokenNames.RBRACE); }
    ","  { debug("COMMA");     return symbol(TokenNames.COMMA); }
    "."  { debug("DOT");       return symbol(TokenNames.DOT); }
    ";"  { debug("SEMICOLON"); return symbol(TokenNames.SEMICOLON); }

    /* Comments - skip */
    {LINE_COMMENT}  { debug("LINE_COMMENT (Skipped)");  /* skip */ }
    {BLOCK_COMMENT} { debug("BLOCK_COMMENT (Skipped)"); /* skip */ }

    /* Invalid/unclosed comments - lexical error */
    "//" { debug("INVALID COMMENT (Partial Line)");  throw new RuntimeException("LEX_ERROR"); }
    "/*" { debug("INVALID COMMENT (Partial Block)"); throw new RuntimeException("LEX_ERROR"); }

    /* Invalid numbers with leading zeros */
    {INVALID_NUMBER} { debug("INVALID_NUMBER"); throw new RuntimeException("LEX_ERROR"); }

    /* Valid integers - check range 0 to 32767 */
    {INTEGER} {
        try {
            debug("INT (Pre-check)");
            int val = Integer.parseInt(yytext());
            if (val < 0 || val > 32767) {
                debug("INT (Out of Range)");
                throw new NumberFormatException("Value exceeds L language limit");
            }
            return symbol(TokenNames.INT, Integer.valueOf(val));
        } catch (NumberFormatException e) {
            throw new RuntimeException("LEX_ERROR");
        }
    }

    /* Strings - return the full string including quotes (parser can strip if needed) */
    {STRING} {
        debug("STRING");
        return symbol(TokenNames.STRING, yytext());
    }

    /* Identifiers - return the name */
    {IDENTIFIER} {
        debug("ID");
        return symbol(TokenNames.ID, yytext());
    }

    /* Whitespace - skip */
    {WhiteSpace} { debug("WhiteSpace (Skipped)"); /* skip */ }

    /* End of file markers */
    {DOLLAR_SIGN} { debug("DOLLAR_SIGN"); return symbol(TokenNames.EOF); }
    <<EOF>>       { debug("EOF");          return symbol(TokenNames.EOF); }

    /* Catch-all: any unrecognized character is a lexical error */
    . { debug("CATCH-ALL (NO MATCH)"); throw new RuntimeException("LEX_ERROR"); }
}
