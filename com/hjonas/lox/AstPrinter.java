package com.hjonas.lox;

import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;

public class AstPrinter {

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
		if (expr == null) {
			return "";
		}

		String str = "";

		if (expr instanceof Unary) {
			str = visitUnary((Unary) expr);
		} else if (expr instanceof Binary) {
			str = visitBinary((Binary) expr);
		} else if (expr instanceof Grouping) {
			str = visitGroupping((Grouping) expr);
		} else if (expr instanceof Literal) {
			str = visitLiteral((Literal) expr);
		}

		return str;
	}

	public String visitUnary(Unary unary) {
		return String.format("(%s %s)", unary.operator.lexeme,
				print(unary.right));
	}

	public String visitBinary(Binary bin) {
		return String.format("(%s %s %s)", bin.operator.lexeme,
				print(bin.left), print(bin.right));
	}

	public String visitGroupping(Grouping group) {
		return String.format("(g: %s)", print(group.expression));
	}

	public String visitLiteral(Literal lit) {
		if (lit.value == null) {
			return "nil";
		}
		return lit.value.toString();
	}
}
