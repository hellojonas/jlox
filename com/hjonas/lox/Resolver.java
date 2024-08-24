package com.hjonas.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.hjonas.lox.Expr.Assign;
import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Call;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;
import com.hjonas.lox.Expr.VariableExpr;
import com.hjonas.lox.Stmt.Block;
import com.hjonas.lox.Stmt.BreakStmt;
import com.hjonas.lox.Stmt.Expression;
import com.hjonas.lox.Stmt.Function;
import com.hjonas.lox.Stmt.IfStmt;
import com.hjonas.lox.Stmt.Print;
import com.hjonas.lox.Stmt.ReturnStmt;
import com.hjonas.lox.Stmt.VariableStmt;
import com.hjonas.lox.Stmt.WhileStmt;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
	private final Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();
	private FunctionType currentFunction = FunctionType.NONE;

	private enum FunctionType {
		NONE,
		FUNCTION
	}

	Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	void resolve(List<Stmt> statements) {
		for (Stmt statement : statements) {
			resolve(statement);
		}
	}

	private void resolve(Stmt statement) {
		statement.accept(this);
	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	private void beginScope() {
		scopes.push(new HashMap<>());
	}

	private void endScope() {
		scopes.pop();
	}

	private void declare(Token name) {
		if (scopes.isEmpty()) {
			return;
		}

		Map<String, Boolean> scope = scopes.peek();

		if (scope.containsKey(name.lexeme)) {
			Lox.error(name, "Already a variable with this name in this scope.");
		}

		scope.put(name.lexeme, false);
	}

	private void define(Token name) {
		if (scopes.isEmpty()) {
			return;
		}

		scopes.peek().put(name.lexeme, true);
	}

	private void resolveLocal(Expr expr, Token name) {
		for (int i = scopes.size() - 1; i >= 0; i--) {
			if (scopes.get(i).containsKey(name.lexeme)) {
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
	}

	private void resolveFunction(Stmt.Function function, FunctionType funcType) {
		FunctionType enclosingFunction = currentFunction;
		currentFunction = funcType;

		beginScope();
		for (Token param : function.params) {
			declare(param);
			define(param);
		}
		resolve(function.body);
		endScope();

		currentFunction = enclosingFunction;
	}

	@Override
	public Void visitExpression(Expression expr) {
		resolve(expr.expr);
		return null;
	}

	@Override
	public Void visitPrint(Print print) {
		resolve(print.expr);
		return null;
	}

	@Override
	public Void visitVariableStmt(VariableStmt var) {
		declare(var.name);

		if (var.initializer != null) {
			resolve(var.initializer);
		}

		define(var.name);
		return null;
	}

	@Override
	public Void visitBlock(Block block) {
		beginScope();
		resolve(block.statements);
		endScope();
		return null;
	}

	@Override
	public Void visitIfStmt(IfStmt ifStmt) {
		resolve(ifStmt.condition);
		resolve(ifStmt.thenBranch);

		if (ifStmt.elseBranch != null) {
			resolve(ifStmt.elseBranch);
		}

		return null;
	}

	@Override
	public Void visitWhileStmt(WhileStmt whileStmt) {
		resolve(whileStmt.condition);
		resolve(whileStmt.body);
		return null;
	}

	@Override
	public Void visitBreakStmt(BreakStmt breakStmt) {
		return null;
	}

	@Override
	public Void visitFunctionStmt(Function function) {
		declare(function.name);
		define(function.name);
		resolveFunction(function, FunctionType.FUNCTION);
		return null;
	}

	@Override
	public Void visitReturnStmt(ReturnStmt returnStmt) {
		if (currentFunction == FunctionType.NONE) {
			Lox.error(returnStmt.token, "Can't return fromm top-level code.");
		}
		if (returnStmt.value != null) {
			resolve(returnStmt.value);
		}
		return null;
	}

	@Override
	public Void visitUnary(Unary unary) {
		resolve(unary.right);
		return null;
	}

	@Override
	public Void visitBinary(Binary binary) {
		resolve(binary.left);
		resolve(binary.right);
		return null;
	}

	@Override
	public Void visitGroupping(Grouping grouping) {
		resolve(grouping.expr);
		return null;
	}

	@Override
	public Void visitLiteral(Literal literal) {
		return null;
	}

	@Override
	public Void visitVariableExpr(VariableExpr variable) {
		if (!scopes.isEmpty()
				&& scopes.peek().get(variable.name.lexeme) == Boolean.FALSE) {
			Lox.error(variable.name, "Can't read local variable in its own initializer.");
		}
		resolveLocal(variable, variable.name);
		return null;
	}

	@Override
	public Void visitAssign(Assign assign) {
		resolve(assign.value);
		resolveLocal(assign, assign.name);
		return null;
	}

	@Override
	public Void visitCall(Call call) {
		resolve(call.callee);
		for (Expr arg : call.arguments) {
			resolve(arg);
		}
		return null;
	}
}
