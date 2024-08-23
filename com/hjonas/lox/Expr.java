package com.hjonas.lox;

import java.util.List;

import com.hjonas.lox.Stmt.Variable;

public abstract class Expr {

	static interface Visitor<R> {
		R visitUnary(Unary unary);

		R visitBinary(Binary binary);

		R visitGroupping(Grouping grouping);

		R visitLiteral(Literal literal);

		R visitVariable(Variable variable);

		R visitAssign(Assign assign);

		R visitCall(Call call);
	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Unary extends Expr {
		final Token operator;
		final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnary(this);
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
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinary(this);
		}
	}

	static class Grouping extends Expr {
		final Expr expr;

		Grouping(Expr expr) {
			this.expr = expr;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupping(this);
		}
	}

	static class Literal extends Expr {
		final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteral(this);
		}
	}

	static class Variable extends Expr {
		final Token name;

		Variable(Token name) {
			this.name = name;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariable(this);
		}
	}

	static class Assign extends Expr {
		final Token name;
		final Expr value;

		Assign(Token name, Expr value) {
			this.name = name;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssign(this);
		}
	}

	static class Call extends Expr {
		final Token paren;
		final Expr callee;
		final List<Expr> arguments;

		Call(Expr callee, Token paren, List<Expr> arguments) {
			this.arguments = arguments;
			this.callee = callee;
			this.paren = paren;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCall(this);
		}
	}
}
