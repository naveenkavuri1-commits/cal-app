package com.example.compkncalculator;

import java.util.Locale;

public class compknMathEngine {
	public static double evaluate(String expression) {
		return new Parser(expression).parse();
	}

	private static class Parser {
		private final String str;
		private int pos = -1;
		private int ch;

		Parser(String s) {
			this.str = s.replaceAll("\\s+", "");
		}

		double parse() {
			nextChar();
			double x = parseExpression();
			if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
			return x;
		}

		private void nextChar() {
			ch = (++pos < str.length()) ? str.charAt(pos) : -1;
		}

		private boolean eat(int charToEat) {
			while (ch == ' ') nextChar();
			if (ch == charToEat) { nextChar(); return true; }
			return false;
		}

		private double parseExpression() {
			double x = parseTerm();
			for (;;) {
				if (eat('+')) x += parseTerm();
				else if (eat('-')) x -= parseTerm();
				else return x;
			}
		}

		private double parseTerm() {
			double x = parseFactor();
			for (;;) {
				if (eat('*')) x *= parseFactor();
				else if (eat('/')) x /= parseFactor();
				else if (eat('%')) x = x / 100.0;
				else return x;
			}
		}

		private double parseFactor() {
			if (eat('+')) return parseFactor();
			if (eat('-')) return -parseFactor();

			double x;
			int startPos = this.pos;
			if (eat('(')) {
				x = parseExpression();
				if (!eat(')')) throw new RuntimeException("Missing )");
			} else if ((ch >= '0' && ch <= '9') || ch == '.') {
				while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
				x = Double.parseDouble(str.substring(startPos, this.pos));
			} else if (Character.isLetter(ch)) {
				while (Character.isLetterOrDigit(ch) || ch == '_') nextChar();
				String func = str.substring(startPos, this.pos).toLowerCase(Locale.US);
				if (func.equals("pi")) return Math.PI;
				if (func.equals("e")) return Math.E;
				if (eat('(')) {
					double arg = parseExpression();
					if (!eat(')')) throw new RuntimeException("Missing ) after arg");
					switch (func) {
						case "sin": return Math.sin(arg);
						case "cos": return Math.cos(arg);
						case "tan": return Math.tan(arg);
						case "asin": return Math.asin(arg);
						case "acos": return Math.acos(arg);
						case "atan": return Math.atan(arg);
						case "sqrt": return Math.sqrt(arg);
						case "ln": return Math.log(arg);
						case "log": return Math.log10(arg);
						case "exp": return Math.exp(arg);
						default: throw new RuntimeException("Unknown func: " + func);
					}
				} else if (eat('^')) {
					double pow = parseFactor();
					return Math.pow(resolveVariable(func), pow);
				} else {
					return resolveVariable(func);
				}
			} else {
				throw new RuntimeException("Unexpected: " + (char) ch);
			}

			while (eat('!')) x = factorial(x);
			if (eat('^')) x = Math.pow(x, parseFactor());
			return x;
		}

		private double resolveVariable(String name) {
			if (name.equals("pi")) return Math.PI;
			if (name.equals("e")) return Math.E;
			throw new RuntimeException("Unknown symbol: " + name);
		}

		private double factorial(double n) {
			if (n < 0) throw new RuntimeException("Factorial of negative");
			long nn = Math.round(n);
			if (Math.abs(n - nn) > 1e-9) throw new RuntimeException("Factorial of non-integer");
			double r = 1.0;
			for (long i = 2; i <= nn; i++) r *= i;
			return r;
		}
	}
}