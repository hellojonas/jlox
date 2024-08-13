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

	Object get(Token name) {
		if (!env.containsKey(name.lexeme)) {
			throw new RuntimeError(name, "variable not initialized.");
		}
		return env.get(name.lexeme);
	}
}
