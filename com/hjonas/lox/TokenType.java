package com.hjonas.lox;

public enum TokenType {
	COMMA,
	SEMICOLON,
	DOT,
	MINUS,
	PLUS,
	STAR,
	LEFT_BRACE,
	RIGHT_BRACE,
	LEFT_PAREN,
	RIGHT_PAREN,

	SLASH,
	BANG,
	BANG_EQUAL,
	EQUAL,
	EQUAL_EQUAL,
	LESS,
	LESS_EQUAL,
	GREATER,
	GREATER_EQUAL,

	IF,
	ELSE,
	WHILE,
	FOR,
	CLASS,
	TRUE,
	FALSE,
	OR,
	AND,
	PRINT,
	THIS,
	SUPER,
	VAR,
	FUN,
	RETURN,
	NIL,

	IDENTIFIER,
	STRING,
	NUMBER,

	BREAK,
	CONTINUE,

	EOF
}
