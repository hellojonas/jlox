package com.hjonas.lox;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import com.hjonas.lox.Expr.Assign;
import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;
import com.hjonas.lox.Stmt.Expression;
import com.hjonas.lox.Stmt.Print;
import com.hjonas.lox.Stmt.Variable;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	Environment env = new Environment();

	private boolean isTruthy(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return (boolean) value;
		}
		return true;
	}

	private String stringify(Object value) {
		if (value == null) {
			return "nil";
		}

		if (value instanceof Double) {
			String text = value.toString();
			if (text.endsWith(".0")) {
				return text.substring(0, text.length() - 2);
			}
		}

		return value.toString();
	}

	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null) {
			return false;
		}
		if (left == null) {
			return false;
		}
		return left.equals(right);
	}

	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "operand must be a number.");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (right instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "operand must be a number.");
	}

	void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				execute(statement);
			}
		} catch (RuntimeError e) {
			Lox.runtimeError(e);
		}
	}

	private void execute(Stmt statement) {
		statement.accept(this);
	}

	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	@Override
	public Object visitUnary(Unary unary) {
		Object right = evaluate(unary.right);

		switch (unary.operator.type) {
			case MINUS: {
				checkNumberOperand(unary.operator, right);
				return -(double) right;
			}
			case BANG: {
				checkNumberOperand(unary.operator, right);
				return !isTruthy(right);
			}
		}

		return null;
	}

	@Override
	public Object visitBinary(Binary binary) {
		Object left = evaluate(binary.left);
		Object right = evaluate(binary.right);

		switch (binary.operator.type) {
			case MINUS: {
				checkNumberOperands(binary.operator, left, right);
				return (double) left - (double) right;
			}
			case STAR: {
				checkNumberOperands(binary.operator, left, right);
				return (double) left * (double) right;
			}
			case SLASH: {
				if (right instanceof Double && (double) right == 0) {
					throw new RuntimeError(binary.operator, "division by zero.");
				}
				checkNumberOperands(binary.operator, left, right);
				return (double) left / (double) right;
			}
			case PLUS: {
				if (left instanceof String || right instanceof String) {
					return stringify(left) + stringify(right);
				} else if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				} else {
					throw new RuntimeError(binary.operator,
							"operands must be two strings or two numbers");
				}
			}
			case GREATER: {
				checkNumberOperands(binary.operator, left, right);
				return (double) right > (double) left;
			}
			case GREATER_EQUAL: {
				checkNumberOperands(binary.operator, left, right);
				return (double) right >= (double) left;
			}
			case LESS: {
				checkNumberOperands(binary.operator, left, right);
				return (double) right < (double) left;
			}
			case LESS_EQUAL: {
				checkNumberOperands(binary.operator, left, right);
				return (double) right <= (double) left;
			}
			case EQUAL_EQUAL: {
				return isEqual(left, right);
			}
			case BANG_EQUAL: {
				return !isEqual(left, right);
			}
		}

		return null;
	}

	@Override
	public Object visitGroupping(Grouping grouping) {
		return evaluate(grouping.expr);
	}

	@Override
	public Object visitLiteral(Literal literal) {
		return literal.value;
	}

	@Override
	public Void visitExpression(Expression expr) {
		evaluate(expr.expr);
		return null;
	}

	@Override
	public Void visitPrint(Print expr) {
		Object e = evaluate(expr.expr);
		System.out.println(stringify(e));
		return null;
	}

	@Override
	public Object visitVariable(com.hjonas.lox.Expr.Variable variable) {
		return env.get(variable.name);
	}

	@Override
	public Void visitVariable(Variable var) {
		Object initializer = null;
		if (var.initializer != null) {
			initializer = evaluate(var.initializer);
		}
		env.define(var.name.lexeme, initializer);
		return null;
	}

	@Override
	public Object visitAssign(Assign assign) {
		return env.assign(assign.name, evaluate(assign.value));
	}
}
