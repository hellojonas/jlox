package com.hjonas.lox;

import java.util.List;

import com.hjonas.lox.Stmt.Function;

class LoxFunction implements LoxCallable {
	final Function declaration;
	private final Environment closure;

	LoxFunction(Function declaration, Environment closure) {
		this.declaration = declaration;
		this.closure = closure;
	}

	@Override
	public int arity() {
		return declaration.params.size();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		final Environment env = new Environment(closure);

		for (int i = 0; i < declaration.params.size(); i++) {
			env.define(declaration.params.get(i).lexeme, arguments.get(i));
		}

		try {
			interpreter.executeBlock(((Stmt.Block) declaration.body).statements, env);
		} catch (Return e) {
			return e.value;
		}

		return null;
	}

	@Override
	public String toString() {
		return "<fn " + declaration.name.lexeme + ">";
	}
}
