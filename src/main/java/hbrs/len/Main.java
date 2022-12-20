package hbrs.len;

import hbrs.len.Datastructures.AST;
import hbrs.len.LogikModul.Expression;
import hbrs.len.Parser.Parser;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		testParserBooleanValues();
	}

	public static void testParser2ExpressionBackToString() {
		String f = "(a&b)";
		Expression e = Parser.parseExpression(f);
		System.out.println(e);
		String s = Parser.expressionToString(e);
		System.out.println(s);
	}

	public static void testParserBooleanValues() {
		String f = "a&b&c";
		Expression e = Parser.parseExpression(f);

		boolean[] belegung = {true, true, true};
		char[] vars = Parser.getAllVariablesInOrder(f);

		boolean ergebnis = Parser.parseExpressionToBoolean(e, vars, belegung);
		System.out.println(ergebnis);
	}
	public static void printUsage() {
		String usage = "Usage\n\n"
			+ "A valid expression looks like some of the following:\n"
			+ "--------------\n"
			+ "a\n"
			+ "(a)\n"
			+ "!(a)\n"
			+ "a&b\n"
			+ "!(a&b)\n"
			+ "a&b&c\n"
			+ "a&b|c\n"
			+ "a&(b|c)\n"
			+ "...\n"
			+ "--------------\n"
			+ "Any expression could have (...) around the whole expression\n"
			+ "Note that brackets has to follow after a '!'";
		System.out.println(usage);
	}
}