package com.hjonas.lox;

import static com.hjonas.lox.TokenType.AND;
import static com.hjonas.lox.TokenType.BANG;
import static com.hjonas.lox.TokenType.BANG_EQUAL;
import static com.hjonas.lox.TokenType.ELSE;
import static com.hjonas.lox.TokenType.EOF;
import static com.hjonas.lox.TokenType.EQUAL;
import static com.hjonas.lox.TokenType.EQUAL_EQUAL;
import static com.hjonas.lox.TokenType.FALSE;
import static com.hjonas.lox.TokenType.GREATER;
import static com.hjonas.lox.TokenType.GREATER_EQUAL;
import static com.hjonas.lox.TokenType.IDENTIFIER;
import static com.hjonas.lox.TokenType.IF;
import static com.hjonas.lox.TokenType.LEFT_BRACE;
import static com.hjonas.lox.TokenType.LEFT_PAREN;
import static com.hjonas.lox.TokenType.LESS;
import static com.hjonas.lox.TokenType.LESS_EQUAL;
import static com.hjonas.lox.TokenType.MINUS;
import static com.hjonas.lox.TokenType.NIL;
import static com.hjonas.lox.TokenType.NUMBER;
import static com.hjonas.lox.TokenType.OR;
import static com.hjonas.lox.TokenType.PLUS;
import static com.hjonas.lox.TokenType.PRINT;
import static com.hjonas.lox.TokenType.RIGHT_BRACE;
import static com.hjonas.lox.TokenType.RIGHT_PAREN;
import static com.hjonas.lox.TokenType.SEMICOLON;
import static com.hjonas.lox.TokenType.SLASH;
import static com.hjonas.lox.TokenType.STAR;
import static com.hjonas.lox.TokenType.STRING;
import static com.hjonas.lox.TokenType.TRUE;
import static com.hjonas.lox.TokenType.WHILE;

import java.util.ArrayList;
import java.util.List;

import com.hjonas.lox.Stmt.WhileStmt;

class Parser {
	private static class ParseError extends RuntimeException {
	}

	private final List<Token> tokens;
	private int current;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.current = 0;
	}

	Stmt declaration() {
		try {
			switch (peek().type) {
				case VAR: {
					advance();
					return varDeclaration();
				}
			}

			return statement();
		} catch (RuntimeError e) {
			synchronize();
			return null;
		}
	}

	Stmt varDeclaration() {
		Token identifier = consume(IDENTIFIER, "expected identifier.");
		Expr initializer = null;

		if (match(EQUAL)) {
			advance();
			initializer = expression();
		}

		consume(SEMICOLON, "expected ';' after variable declaration.");
		return new Stmt.Variable(identifier, initializer);
	}

	Stmt statement() {
		if (match(PRINT)) {
			advance();
			return print();
		}

		if (match(IF)) {
			advance();
			return ifStatement();
		}

		if (match(LEFT_BRACE)) {
			advance();
			Stmt b = block();
			return b;
		}

		if (match(WHILE)) {
			advance();
			Stmt w = whileStmt();
			return w;
		}

		Expr expr = expression();
		consume(SEMICOLON, "expected ';' after expression.");
		return new Stmt.Expression(expr);
	}

	Stmt whileStmt() {
		consume(LEFT_PAREN, "expected '(' after while statement.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "expected ')' after conditiion.");

		Stmt body = statement();

		return new Stmt.WhileStmt(condition, body);
	}

	Stmt ifStatement() {
		consume(LEFT_PAREN, "expected '(' after if statement.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "expected ')' after expression.");

		Stmt thenBranch = statement();
		Stmt elseBranch = null;

		if (match(ELSE)) {
			advance();
			elseBranch = statement();
		}

		return new Stmt.IfStmt(condition, thenBranch, elseBranch);
	}

	Stmt block() {
		List<Stmt> statments = new ArrayList<>();
		while (!match(RIGHT_BRACE)) {
			statments.add(declaration());
		}
		consume(RIGHT_BRACE, "expected } after block.");
		return new Stmt.Block(statments);
	}

	Stmt print() {
		Expr expr = expression();
		consume(SEMICOLON, "expected ';' after expression.");
		return new Stmt.Print(expr);
	}

	Expr expression() {
		return assignment();
	}

	Expr assignment() {
		Expr expr = or();

		if (match(EQUAL)) {
			Token token = advance();
			Expr value = or();

			if (expr instanceof Expr.Variable) {
				return new Expr.Assign(((Expr.Variable) expr).name, value);
			}

			Lox.error(token, "invalid assignment identifier");
		}

		return expr;
	}

	Expr or() {
		Expr expr = and();

		while (match(OR)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, and());
		}

		return expr;
	}

	Expr and() {
		Expr expr = equality();

		while (match(AND)) {
			Token operator = advance();
			expr = new Expr.Binary(operator, expr, equality());
		}

		return expr;
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
		if (match(IDENTIFIER)) {
			return new Expr.Variable(advance());
		}

		if (match(LEFT_PAREN)) {
			advance();
			Expr expr = expression();
			consume(RIGHT_PAREN, "expected ')'.");
			return expr;
		}

		throw error(peek(), "expected expression.");
	}

	private Token consume(TokenType type, String message) {
		if (peek().type.equals(type)) {
			return advance();
		}
		throw error(peek(), message);
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

	List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();

		while (!isAtEnd()) {
			statements.add(declaration());
		}

		return statements;
	}

	private void synchronize() {
		while (!isAtEnd()) {
			if (match(SEMICOLON)) {
				advance();
				return;
			}

			switch (peek().type) {
				case IF:
				case WHILE:
				case FOR:
				case CLASS:
				case PRINT:
				case FUN: {
					return;
				}
			}
			advance();
		}
	}
}
