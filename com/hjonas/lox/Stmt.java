package com.hjonas.lox;

import java.util.List;

abstract class Stmt {

	static interface Visitor<R> {
		R visitExpression(Expression expr);

		R visitPrint(Print print);

		R visitVariable(Variable var);

		R visitBlock(Block block);

		R visitIfStmt(IfStmt ifStmt);
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

	static class IfStmt extends Stmt {
		final Expr condition;
		final Stmt thenBranch;
		final Stmt elseBranch;

		IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}
	}
}
