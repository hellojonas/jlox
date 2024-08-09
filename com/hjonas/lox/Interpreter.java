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
				return -(double) right;
			}
			case BANG: {
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
				return (double) left - (double) right;
			}
			case STAR: {
				return (double) left * (double) right;
			}
			case SLASH: {
				return (double) left / (double) right;
			}
			case PLUS: {
				if (left instanceof String && right instanceof String) {
					return (String) left + (String) right;
				} else if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				}
			}
			case GREATER: {
				return (double) right > (double) left;
			}
			case GREATER_EQUAL: {
				return (double) right >= (double) left;
			}
			case LESS: {
				return (double) right < (double) left;
			}
			case LESS_EQUAL: {
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
}
