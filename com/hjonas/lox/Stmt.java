package com.hjonas.lox;

abstract class Stmt {

	static interface Visitor<R> {
		R visitExpression(Expression expr);
		R visitPrint(Print expr);
	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Expression extends Stmt {
		final Expr expr;

		Expression(Expr expr) {
			this.expr = expr;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpression(this);
		}
	}

	static class Print extends Stmt {
		final Expr expr;

		Print(Expr expr) {
			this.expr = expr;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitPrint(this);
		}
	}
}
