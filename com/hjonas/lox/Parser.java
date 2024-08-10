package com.hjonas.lox;

import static com.hjonas.lox.TokenType.BANG;
import static com.hjonas.lox.TokenType.BANG_EQUAL;
import static com.hjonas.lox.TokenType.EOF;
import static com.hjonas.lox.TokenType.EQUAL_EQUAL;
import static com.hjonas.lox.TokenType.FALSE;
import static com.hjonas.lox.TokenType.GREATER;
import static com.hjonas.lox.TokenType.GREATER_EQUAL;
import static com.hjonas.lox.TokenType.LEFT_PAREN;
import static com.hjonas.lox.TokenType.LESS;
import static com.hjonas.lox.TokenType.LESS_EQUAL;
import static com.hjonas.lox.TokenType.MINUS;
import static com.hjonas.lox.TokenType.NIL;
import static com.hjonas.lox.TokenType.NUMBER;
import static com.hjonas.lox.TokenType.PLUS;
import static com.hjonas.lox.TokenType.RIGHT_PAREN;
import static com.hjonas.lox.TokenType.SLASH;
import static com.hjonas.lox.TokenType.STAR;
import static com.hjonas.lox.TokenType.STRING;
import static com.hjonas.lox.TokenType.TRUE;

import java.util.List;

class Parser {
	private static class ParseError extends RuntimeException {
	}

	private final List<Token> tokens;
	private int current;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.current = 0;
	}

	Expr expression() {
		return equality();
	}

	Expr equality() {
		Expr expr = comparison();

		while (match(EQUAL_EQUAL, BANG_EQUAL)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, comparison());
		}

		return expr;
	}

	Expr comparison() {
		Expr expr = term();

		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, term());
		}

		return expr;
	}

	Expr term() {
		Expr expr = factor();

		while (match(PLUS, MINUS)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, factor());
		}

		return expr;
	}

	Expr factor() {
		Expr expr = unary();

		while (match(STAR, SLASH)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, unary());
		}

		return expr;
	}

	Expr unary() {
		if (match(BANG, MINUS)) {
			Token operator = advance();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}

		return primary();
	}

	Expr primary() {
		if (match(TRUE)) {
			advance();
			return new Expr.Literal(true);
		}
		if (match(FALSE)) {
			advance();
			return new Expr.Literal(false);
		}
		if (match(NIL)) {
			advance();
			return new Expr.Literal(null);
		}
		if (match(NUMBER)) {
			return new Expr.Literal(advance().literal);
		}
		if (match(STRING)) {
			return new Expr.Literal(advance().literal);
		}

		if (match(LEFT_PAREN)) {
			advance();
			Expr expr = expression();

			if (!peek().type.equals(RIGHT_PAREN)) {
				throw error(peek(), "expectd ).");
			} else {
				advance();
				return expr;
			}
		}

		throw error(peek(), "expected expression.");
	}

	private Token advance() {
		return tokens.get(current++);
	}

	private Token peek() {
		return tokens.get(current);
	}

	private boolean isAtEnd() {
		return peek().type.equals(EOF);
	}

	private boolean match(TokenType... types) {
		if (isAtEnd()) {
			return false;
		}

		for (TokenType type : types) {
			if (peek().type.equals(type)) {
				return true;
			}
		}
		return false;
	}

	private ParseError error(Token token, String message) {
		Lox.error(token.line, message);
		return new ParseError();
	}

	Expr parse() {
		try {
			return expression();
		} catch (Exception e) {
			return null;
		}
	}
}
