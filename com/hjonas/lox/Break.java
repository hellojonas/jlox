package com.hjonas.lox;

class Break extends RuntimeException {
	final Token token;

	Break(Token token, String message) {
		super(message, null, false, false);
		this.token = token;
	}
}
