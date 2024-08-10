package com.hjonas.lox;

import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;

class Interpreter implements Expr.Visitor<Object> {

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

	private boolean isTruthy(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return (boolean) value;
		}
		return true;
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
				checkNumberOperands(binary.operator, left, right);
				return (double) left / (double) right;
			}
			case PLUS: {
				if (left instanceof String && right instanceof String) {
					return (String) left + (String) right;
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

	void interpret(Expr expression) {
		try {
			Object value = evaluate(expression);
			System.out.println(stringify(value));
		} catch (RuntimeError e) {
			Lox.runtimeError(e);
		}
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

	@Override
	public Object visitGroupping(Grouping grouping) {
		return evaluate(grouping.expr);
	}

	@Override
	public Object visitLiteral(Literal literal) {
		return literal.value;
	}

	Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "operand must be a number.");
	}

	void checkNumberOperands(Token operator, Object left, Object right) {
		if (right instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "operand must be a number.");
	}
}
