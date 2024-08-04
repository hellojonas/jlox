package com.hjonas.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
	private String source;
	private List<Token> tokens;
	private int line;
	private int start;
	private int cursor;

	Scanner(String source) {
		this.source = source;
		line = 1;
		start = 0;
		cursor = 0;
		tokens = new ArrayList<>();
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			char ch = advance();

			switch (ch) {
				case '\n': {
					line++;
					start = cursor;
					continue;
				}
				case ' ': {
					start = cursor;
					continue;
				}
				case ',': {
					addToken(TokenType.COMMA, null);
					continue;
				}
				case ';': {
					addToken(TokenType.SEMICOLON, null);
					continue;
				}
				case '.': {
					addToken(TokenType.DOT, null);
					continue;
				}
				case '-': {
					addToken(TokenType.MINUS, null);
					continue;
				}
				case '+': {
					addToken(TokenType.PLUS, null);
					continue;
				}
				case '*': {
					addToken(TokenType.STAR, null);
					continue;
				}
				case '{': {
					addToken(TokenType.LEFT_BRACE, null);
					continue;
				}
				case '}': {
					addToken(TokenType.RIGHT_BRACE, null);
					continue;
				}
				case '(': {
					addToken(TokenType.LEFT_PAREN, null);
					continue;
				}
				case ')': {
					addToken(TokenType.RIGHT_PAREN, null);
					continue;
				}
				case '/': {
					if (!matchAdvance('/')) {
						addToken(TokenType.SLASH, null);
						continue;
					}
					comment();
					continue;
				}
				case '!': {
					if (!matchAdvance('=')) {
						addToken(TokenType.BANG, null);
						continue;
					}
					addToken(TokenType.BANG_EQUAL, null);
					continue;
				}
				case '=': {
					if (!matchAdvance('=')) {
						addToken(TokenType.EQUAL, null);
						continue;
					}
					addToken(TokenType.EQUAL_EQUAL, null);
					continue;
				}
				case '<': {
					if (!matchAdvance('=')) {
						addToken(TokenType.LESS, null);
						continue;
					}
					addToken(TokenType.LESS_EQUAL, null);
					continue;
				}
				case '>': {
					if (!matchAdvance('=')) {
						addToken(TokenType.GREATER, null);
						continue;
					}
					addToken(TokenType.GREATER_EQUAL, null);
					continue;
				}
				case '"': {
					string();
					continue;
				}
				default: {
					if (isDigit(ch)) {
						digits();
						continue;
					} else if (isAlphaNum(ch)) {
						identifier();
						continue;
					}
					Lox.error(line, ("unexpected character"));
				}
			}
		}

		addToken(TokenType.EOF, "\0", null);
		return tokens;
	}

	private void digits() {
		while (!isAtEnd()) {
			char ch = peek();
			if (isDigit(ch)) {
				advance();
				continue;
			} else if (ch == '.' && isDigit(peekNext())) {
				advance();
				continue;
			}
			break;
		}

		String lexeme = source.substring(start, cursor);
		start = cursor;

		try {
			addToken(TokenType.NUMBER, lexeme, Double.parseDouble(lexeme));
		} catch (NumberFormatException e) {
			Lox.error(line, "invalid number");
		}
	}

	private void identifier() {
		while (!isAtEnd() && isAlphaNum(peek())) {
			advance();
		}

		String str = source.substring(start, cursor);
		TokenType tType = keywords.get(str);

		if (tType != null) {
			addToken(tType, null);
			return;
		}

		addToken(TokenType.IDENTIFIER, null);
	}

	private void string() {
		char ch = peek();

		while (ch != '"') {
			ch = advance();

			if (isAtEnd() && ch != '"') {
				Lox.error(line, "unterminated string");
			}
		}

		String lexeme = source.substring(start + 1, cursor - 1);
		addToken(TokenType.STRING, lexeme, lexeme);
	}

	private void comment() {
		while (!isAtEnd() && advance() != '\n') {
			continue;
		}
		line++;
		start = cursor;
	}

	private char peek() {
		return source.charAt(cursor);
	}

	private char peekNext() {
		return source.charAt(cursor + 1);
	}

	private boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	private boolean isAlpha(char ch) {
		return ch == '_'
				|| (ch >= 'a' && ch <= 'z')
				|| (ch >= 'A' && ch <= 'Z');
	}

	private boolean isAlphaNum(char ch) {
		return isAlpha(ch) || isDigit(ch);
	}

	private void addToken(TokenType type, String literal) {
		String lexeme = source.substring(start, cursor);
		addToken(type, lexeme, literal);
		start = cursor;
	}

	private void addToken(TokenType type, String lexeme, Object literal) {
		tokens.add(new Token(type, lexeme, literal, line));
	}

	private char advance() {
		return source.charAt(cursor++);
	}

	private boolean matchAdvance(char ch) {
		if (source.charAt(cursor) == ch) {
			cursor++;
			return true;
		}
		return false;
	}

	private boolean isAtEnd() {
		return source.length() <= cursor;
	}

	private static Map<String, TokenType> keywords = new HashMap<>() {
		{
			put("if", TokenType.IF);
			put("else", TokenType.ELSE);
			put("while", TokenType.WHILE);
			put("for", TokenType.FOR);
			put("class", TokenType.CLASS);
			put("true", TokenType.TRUE);
			put("false", TokenType.FALSE);
			put("or", TokenType.OR);
			put("and", TokenType.AND);
			put("print", TokenType.PRINT);
			put("this", TokenType.THIS);
			put("super", TokenType.SUPER);
			put("var", TokenType.VAR);
			put("fun", TokenType.FUN);
			put("return", TokenType.RETURN);
			put("nil", TokenType.NIL);
		}
	};
}
