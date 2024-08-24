package com.hjonas.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
	final Map<String, Object> env;
	private final Environment enclosing;

	Environment() {
		this.env = new HashMap<String, Object>();
		enclosing = null;
	}

	Environment(Environment enclosing) {
		this.env = new HashMap<>();
		this.enclosing = enclosing;
	}

	void define(String name, Object value) {
		env.put(name, value);
	}

	Object assign(Token name, Object value) {
		if (env.containsKey(name.lexeme)) {
			env.put(name.lexeme, value);
			return value;
		}

		if (enclosing != null) {
			return enclosing.assign(name, value);
		}

		throw new RuntimeError(name, "undefined variable");
	}

	Object get(Token name) {
		if (env.containsKey(name.lexeme)) {
			return env.get(name.lexeme);
		}

		if (enclosing != null) {
			return enclosing.get(name);
		}

		throw new RuntimeError(name, "undefined variable");
	}

	public Object getAt(Integer distance, String name) {
		return ancestor(distance).env.get(name);
	}

	public Environment ancestor(int distance) {
		Environment environment = this;

		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

    public void assignAt(Integer distance, Token name, Object value) {
		ancestor(distance).env.put(name.lexeme, value);
    }
}
