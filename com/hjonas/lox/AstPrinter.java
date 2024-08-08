package com.hjonas.lox;

import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;
import com.hjonas.lox.Expr.Visitor;

public class AstPrinter implements Visitor<String> {

	public static void main(String[] args) {
		// (1 + 2) * 3
		Expr.Binary expr = new Expr.Binary(
				new Token(TokenType.STAR, "*", null, 1),
				new Expr.Grouping(new Expr.Binary(
						new Token(TokenType.PLUS, "+", null, 1),
						new Expr.Literal(1),
						new Expr.Literal(2))),
				new Expr.Literal(3));

		System.out.println(new AstPrinter().print(expr));
	}

	String print(Expr expr) {
		return expr.accept(this);
	}

	@Override
	public String visitUnary(Unary unary) {
		return String.format("%s %s", unary.operator.lexeme,
				unary.right.accept(this));
	}

	@Override
	public String visitBinary(Binary bin) {
		return String.format("(%s %s %S)", bin.operator.lexeme, bin.left.accept(this),
				bin.right.accept(this));
	}

	@Override
	public String visitGroupping(Grouping group) {
		return String.format("(g: %s)", group.expr.accept(this));
	}

	@Override
	public String visitLiteral(Literal lit) {
		return lit.value.toString();
	}
}
