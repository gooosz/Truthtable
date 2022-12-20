package hbrs.len;

import hbrs.len.LogikModul.Expression;
import hbrs.len.Parser.Parser;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			printErrorMessage("no expression given");
			printUsage();
			return;
		}
		if (args.length > 1) {
			printErrorMessage("more than 1 expression given");
			printUsage();
			return;
		}
		if (testUserInputForErrors(args[0])) {
			return;
		}
		printTruthTable(Parser.parseExpression(args[0]));
	}

	public static boolean[][] binaryValues(int anzahlVariables) {
		int rows = (int) Math.pow(2, anzahlVariables);

		boolean[][] values = new boolean[rows][anzahlVariables];

		// init at first to all false
		char[] tmpChars = new char[anzahlVariables];
		Arrays.fill(tmpChars, '0');
		String line = new String(tmpChars);

		for (int i=0; i<rows; i++) {
			// every single line
			// create a string of binary values
			if (line.length() != anzahlVariables) {
				System.out.println("line.length() != anzahlVariables at row " + i);
				return null;
			}
			for (int j=0; j<values[i].length; j++) {
				values[i][j] = (line.charAt(j) == '1');
			}
			// update the line by adding 1 in binary to it
			int lineInt = Integer.parseInt(line, 2);
			lineInt += 1;
			line = Integer.toBinaryString(lineInt);
			// insert missing length zeros at front of string
			// no needed increase of k because line.length increases
			for (int k=0; k<anzahlVariables-line.length();) {
				line = "0" + line;
			}
		}
		return values;
	}

	public static boolean testUserInputForErrors(String input) {
		/*
		 * Errors are:
		 *
		 * no input at all
		 * not equal amount of open/closing brackets
		 *
		*/
		if (input == null || input.equals("")) {
			printErrorMessage("empty expression");
			printUsage();
			return true;
		}

		for (int i=0; i<input.length(); i++) {
			if (input.charAt(i) == '(') {
				int closingBracketIndex =
					Parser.getClosingBracketIndexFromOpenBracket(input, i);
				if (closingBracketIndex == -1) {
					printErrorMessage("no closing bracket for bracket at position "
							+ i);
					printUsage();
					return true;
				}
			}
		}
		// input should be ok now
		return false;
	}

	public static void printTruthTable(Expression e) {
		assert(e != null);
		char[] vars = Parser.getAllVariablesInOrder(e);
		String printingLine = String.valueOf(vars[0]);
		for (int i=1; i<vars.length; i++) {
			printingLine += "\t│\t" + vars[i];
		}
		printingLine += "\t│\t" + Parser.expressionToString(e);
		System.out.println(printingLine);
		// next line in output
		printingLine = "";
		for (int i=0; i<50; i++) {
			printingLine += "─";
		}
		System.out.println(printingLine);
		// next line in output
		// first line of boolean values -> everything is false
		boolean[][] booleanValues = binaryValues(vars.length);
		assert(booleanValues != null);
		for (int i=0; i<booleanValues.length; i++) {
			for (int j=0; j<booleanValues[i].length; j++) {
				int value = (booleanValues[i][j]) ? 1 : 0;
				System.out.print(value + "\t");
				if (j < booleanValues[i].length-1) {
					System.out.print("\t");
				}
			}
			boolean ergebnisOfLine = Parser.parseExpressionToBoolean(e, vars, booleanValues[i]);
			int value = (ergebnisOfLine) ? 1 : 0;
			System.out.println("│\t" + value);
		}

	}

	public static void printErrorMessage(String msg) {
		System.out.println("Error: " + msg);
	}

	public static void printUsage() {
		String usage = "\nUsage\n\n"
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