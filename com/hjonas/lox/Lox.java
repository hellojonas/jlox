package com.hjonas.lox;

import static com.hjonas.lox.TokenType.EOF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	private static Interpreter interpreter = new Interpreter();

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox <script>");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	private static void runPrompt() throws IOException {
		InputStreamReader iStreamReader = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(iStreamReader);

		while (true) {
			System.out.print("> ");
			String line = br.readLine();

			if (line == null) {
				break;
			}

			run(line);
			hadError = false;
		}
	}

	private static void runFile(String string) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(string));
		run(new String(bytes, Charset.defaultCharset()));

		if (hadError) {
			System.exit(65);
		}
		if (hadRuntimeError) {
			System.exit(70);
		}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		List<Stmt> statements = new Parser(tokens).parse();

		if (hadError) {
			return;
		}

		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);

		if (hadError) {
			return;
		}

		try {
			interpreter.interpret(statements);
		} catch (RuntimeError e) {
			error(e.token, e.getMessage());
			hadRuntimeError = true;
		} catch (Break e) {
			error(e.token, e.getMessage());
			hadRuntimeError = true;
		}
	}

	static void error(Token token, String message) {
		if (token.type.equals(EOF)) {
			report(token.line, " at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

	public static void runtimeError(RuntimeError e) {
		System.out.println(e.getMessage() + "\n[line " + e.token.line + "]");
		hadRuntimeError = true;
	}
}
