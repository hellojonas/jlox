package com.hjonas.lox;

import static com.hjonas.lox.TokenType.SLASH;
import static com.hjonas.lox.TokenType.STAR;

import com.hjonas.lox.Expr.Binary;
import com.hjonas.lox.Expr.Grouping;
import com.hjonas.lox.Expr.Literal;
import com.hjonas.lox.Expr.Unary;

class Interpreter {
	Interpreter() {
	}

	Object interpret(Expr expr) {
		return evaluate(expr);
	}

	private boolean isThruty(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Boolean) {
			return (boolean) obj;
		}
		return true;
	}

	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null) {
			return true;
		}
		if (left == null) {
			return false;
		}
		return left.equals(right);
	}

	private String stringify(Object obj) {
		if (obj == null) {
			return "nil";
		}
		if (obj instanceof Double) {
			String text = obj.toString();
			if (text.endsWith(".0")) {
				return text.substring(0, text.length() - 2);
			}
			return text;
		}
		return obj.toString();
	}

	private Object evaluate(Expr expr) {
		Object value = null;

		if (expr instanceof Unary) {
			value = visitUnary((Unary) expr);
		}
		if (expr instanceof Binary) {
			value = visitBinary((Binary) expr);
		}
		if (expr instanceof Grouping) {
			value = visitGrouping((Grouping) expr);
		}
		if (expr instanceof Literal) {
			value = visitLiteral((Literal) expr);
		}

		return value;
	}

	private Object visitUnary(Unary unary) {
		switch (unary.operator.type) {
			case MINUS: {
				Object value = evaluate(unary.right);
				return -(double) value;
			}
			case BANG: {
				Object value = evaluate(unary.right);
				return isThruty(value);
			}
		}
		throw new RuntimeError(unary.operator, "unexpected token.");
	}

	private Object visitBinary(Binary binary) {
		Object left = evaluate(binary.left);
		Object right = evaluate(binary.right);

		switch (binary.operator.type) {
			case PLUS: {
				if (left instanceof String || right instanceof String) {
					return stringify(left) + stringify(right);
				}
				if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				}
				throw new RuntimeError(binary.operator, "operands must be string or number.");
			}
			case MINUS: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left - (double) right;
				}
				break;
			}
			case STAR: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left * (double) right;
				}
				break;
			}
			case SLASH: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left / (double) right;
				}
				break;
			}
			case GREATER: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left > (double) right;
				}
				break;
			}
			case GREATER_EQUAL: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left >= (double) right;
				}
				break;
			}
			case LESS: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left < (double) right;
				}
				break;
			}
			case LESS_EQUAL: {
				if (left instanceof Double && right instanceof Double) {
					return (double) left <= (double) right;
				}
				break;
			}
			case BANG_EQUAL: {
				return !isEqual(left, right);
			}
			case EQUAL_EQUAL: {
				return isEqual(left, right);
			}
		}

		throw new RuntimeError(binary.operator, "operands must be two strings or two numbers.");
	}

	private Object visitGrouping(Grouping group) {
		return evaluate(group.expression);
	}

	private Object visitLiteral(Literal literal) {
		return literal.value;
	}
}
