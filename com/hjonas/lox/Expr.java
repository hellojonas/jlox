package com.hjonas.lox;

public abstract class Expr {

	static interface Visitor<R> {
		R visitUnary(Unary unary);

		R visitBinary(Binary binary);

		R visitGroupping(Grouping grouping);

		R visitLiteral(Literal literal);
	}

	abstract <R> R accept(Visitor<R> visiror);

	static class Unary extends Expr {
		final Token operator;
		final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visiror) {
			return visiror.visitUnary(this);
		}
	}

	static class Binary extends Expr {
		final Token operator;
		final Expr left;
		final Expr right;

		Binary(Token operator, Expr left, Expr right) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visiror) {
			return visiror.visitBinary(this);
		}
	}

	static class Grouping extends Expr {
		final Expr expr;

		Grouping(Expr expr) {
			this.expr = expr;
		}

		@Override
		<R> R accept(Visitor<R> visiror) {
			return visiror.visitGroupping(this);
		}
	}

	static class Literal extends Expr {
		final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visiror) {
			return visiror.visitLiteral(this);
		}
	}
}
