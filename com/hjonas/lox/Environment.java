package com.hjonas.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
	private Map<String, Object> env;

	Environment() {
		this.env = new HashMap<String, Object>();
	}

	void define(String name, Object value) {
		env.put(name, value);
	}

	Object assign(Token name, Object value) {
		if (!env.containsKey(name.lexeme)) {
			throw new RuntimeError(name, "undefined variable");
		}

		env.put(name.lexeme, value);
		return value;
	}

	Object get(Token name) {
		if (!env.containsKey(name.lexeme)) {
			throw new RuntimeError(name, "undefined variable");
		}

		return env.get(name.lexeme);
	}
}
