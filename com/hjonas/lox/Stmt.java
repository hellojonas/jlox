package com.hjonas.lox;

import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

abstract class Stmt {

	static interface Visitor<R> {
		R visitExpression(Expression expr);

		R visitPrint(Print print);

		R visitVariable(Variable var);

		R visitBlock(Block block);
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

	static class Variable extends Stmt {
		final Token name;
		final Expr initializer;

		Variable(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariable(this);
		}
	}

	static class Block extends Stmt {
		final List<Stmt> statements;

		Block(List<Stmt> statements) {
			this.statements = statements;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBlock(this);
		}
	}
}
