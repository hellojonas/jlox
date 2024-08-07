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
				new Token(TokenType.STAR, "*", null, 0),
				new Expr.Grouping(new Expr.Binary(
						new Token(TokenType.PLUS, "+", null, 0),
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visitUnary'");
	}

	@Override
	public String visitBinary(Binary unary) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visitBinary'");
	}

	@Override
	public String visitGroupping(Grouping unary) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visitGroupping'");
	}

	@Override
	public String visitLiteral(Literal unary) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visitLiteral'");
	}
}
