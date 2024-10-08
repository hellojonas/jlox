package com.hjonas.lox;

import java.util.List;

abstract class Stmt {

	static interface Visitor<R> {
		R visitExpression(Expression expr);

		R visitPrint(Print print);

		R visitVariableStmt(VariableStmt var);

		R visitBlock(Block block);

		R visitIfStmt(IfStmt ifStmt);

		R visitWhileStmt(WhileStmt whileStmt);

		R visitBreakStmt(BreakStmt breakStmt);

		R visitFunctionStmt(Function function);

		R visitReturnStmt(ReturnStmt returnStmt);
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

	static class VariableStmt extends Stmt {
		final Token name;
		final Expr initializer;

		VariableStmt(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableStmt(this);
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

	static class WhileStmt extends Stmt {
		final Expr condition;
		final Stmt body;

		WhileStmt(Expr condition, Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}
	}

	static class BreakStmt extends Stmt {
		final Token token;

		BreakStmt(Token token) {
			this.token = token;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStmt(this);
		}
	}

	static class Function extends Stmt {
		final Token name;
		final List<Token> params;
		final Stmt body;

		Function(Token name, List<Token> params, Stmt body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}
	}

	static class ReturnStmt extends Stmt {
		final Token token;
		final Expr value;

		ReturnStmt(Token token, Expr value) {
			this.token = token;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}
	}
}
