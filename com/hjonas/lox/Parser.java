package com.hjonas.lox;

import static com.hjonas.lox.TokenType.BANG;
import static com.hjonas.lox.TokenType.BANG_EQUAL;
import static com.hjonas.lox.TokenType.EOF;
import static com.hjonas.lox.TokenType.EQUAL;
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

	private int current;
	private final List<Token> tokens;

	Parser(List<Token> tokens) {
		this.current = 0;
		this.tokens = tokens;
	}

	private boolean isAtEnd() {
		return tokens.get(current).type.equals(EOF);
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token advance() {
		return tokens.get(current++);
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (!isAtEnd() && peek().type.equals(type)) {
				return true;
			}
		}
		return false;
	}

	private void consume(TokenType type, String message) {
		if (peek().type.equals(type)) {
			advance();
			return;
		}
		Lox.error(peek().line, message);
		throw new ParseError();
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
		if (match(BANG, EQUAL)) {
			Token operator = advance();
			Expr operand = unary();
			return new Expr.Unary(operator, operand);
		}

		return primary();
	}

	Expr primary() {
		if (match(NIL)) {
			advance();
			return new Expr.Literal(null);
		}
		if (match(TRUE)) {
			advance();
			return new Expr.Literal(true);
		}
		if (match(FALSE)) {
			advance();
			return new Expr.Literal(false);
		}
		if (match(NUMBER)) {
			return new Expr.Literal((double) advance().literal);
		}
		if (match(STRING)) {
			return new Expr.Literal((String) advance().literal);
		}
		if (match(LEFT_PAREN)) {
			advance();
			Expr expression = new Expr.Grouping(expression());
			consume(RIGHT_PAREN, "expected ')' after expression.");
			return expression;
		}

		Lox.error(peek().line, "expected expression");
		throw new ParseError();
	}

	Expr parse() {
		return expression();
	}
}
