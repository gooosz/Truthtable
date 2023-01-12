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
		//String truthtable = truthtableToString(Parser.parseExpression(args[0]));
		//printTruthTable(Parser.parseExpression(args[0]));
		//Expression e = Parser.parseExpression(args[0]);

		//System.out.println(truthtableToString(e, truthtableAsArray(e)));
		Expression e = Parser.parseExpression(args[0]);
		boolean[][] wtable = truthtableAsArray(e);
		String dnf = equivalentDNF(Parser.getAllVariablesInOrder(e), wtable);
		System.out.println(truthtableToString(e));
		System.out.println("CNF: " + equivalentCNF(Parser.getAllVariablesInOrder(e), wtable));
		System.out.println("DNF: " + equivalentDNF(Parser.getAllVariablesInOrder(e), wtable));
	}

	/**
	 * @param vars are all variables in expression in order
	 * @param wtable is the truthtable values
	 * @return an equivalent CNF to the given expression
	 */
	public static String equivalentCNF(char[] vars, boolean[][] wtable) {
		//TODO doesn't work right yet
		String cnf = "";
		/*
		 * go through evaluation of truthtable
		 * by looping over lines and checking the last entry
		 */
		for (int line=0; line<wtable.length; line++) {
			boolean evaluateLine = wtable[line][wtable[line].length-1];
			if (!evaluateLine) {
				// check for previous "|" in dnf
				if (cnf.length() > 0 && cnf.charAt(cnf.length()-1) != '&') {
					cnf += "&";
				}
				// get all vars of that line
				// those vars form a 'Klausel'
				String klausel = "";
				for (int i=0; i<wtable[line].length-1; i++) {
					if (wtable[line][i]) {
						// negate
						klausel += "!";
					}
					klausel += vars[i];
					if (i < wtable[line].length-1-1) {
						klausel += "|";
					}
				}
				cnf += "(" + klausel + ")";
			}
		}
		return cnf;
	}

	/**
	 * @param vars are the variable in expression in order
	 * @param wtable is the boolean values of truthtable
	 * @return an equivalent DNF to the given expression
	 */
	public static String equivalentDNF(char[] vars, boolean[][] wtable) {
		String dnf = "";
		/*
		 * go through evaluation of truthtable
		 * by looping over lines and checking the last entry
		*/
		for (int line=0; line<wtable.length; line++) {
			boolean evaluateLine = wtable[line][wtable[line].length-1];
			if (evaluateLine) {
				// check for previous "|" in dnf
				if (dnf.length() > 0 && dnf.charAt(dnf.length()-1) != '|') {
					dnf += "|";
				}
				// get all vars of that line
				// those vars form a 'Klausel'
				String klausel = "";
				for (int i=0; i<wtable[line].length-1; i++) {
					if (!wtable[line][i]) {
						// negate
						klausel += "!";
					}
						// negate
					klausel += vars[i];
					if (i < wtable[line].length-1-1) {
						klausel += "&";
					}
				}
				dnf += "(" + klausel + ")";
			}
		}
		return dnf;
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

	public static boolean[][] truthtableAsArray(Expression e) {
		assert(e != null);

		char[] vars = Parser.getAllVariablesInOrder(e);
		boolean[][] wahrheitswerte = new boolean[(int)Math.pow(2, vars.length)][vars.length+1];

		// first line of boolean values -> everything is false
		boolean[][] booleanValues = binaryValues(vars.length);
		assert(booleanValues != null);
		for (int i=0; i<booleanValues.length; i++) {
			for (int j=0; j<booleanValues[i].length; j++) {
				wahrheitswerte[i][j] = booleanValues[i][j];
			}
			boolean ergebnisOfLine = Parser.parseExpressionToBoolean(e, vars, booleanValues[i]);
			wahrheitswerte[i][wahrheitswerte[i].length-1] = ergebnisOfLine;
		}
		return wahrheitswerte;
	}

	public static String truthtableToString(Expression e, boolean[][] w) {
		assert(e != null);
		assert(w != null);
		String truthtable = "";

		char[] vars = Parser.getAllVariablesInOrder(e);
		String printingLine = String.valueOf(vars[0]);
		for (int i=1; i<vars.length; i++) {
			printingLine += "\t│\t" + vars[i];
		}
		printingLine += "\t│\t" + Parser.expressionToString(e);
		truthtable += printingLine + "\n";
		// next line in output
		printingLine = "";
		for (int i=0; i<50; i++) {
			printingLine += "─";
		}
		truthtable += printingLine + "\n";
		// next line in output
		for (int i=0; i<w.length; i++) {
			for (int j=0; j<w[i].length-1; j++) {
				boolean value = w[i][j];
				truthtable += (value) ? 1 : 0;
				truthtable += "\t";
				if (j < w[i].length-2) {
					truthtable += "\t";
				}
			}
			boolean ergebnisOfLine = Parser.parseExpressionToBoolean(e, vars, w[i]);
			int value = (ergebnisOfLine) ? 1 : 0;
			truthtable += "│\t" + value + "\n";
		}
		return truthtable;
	}

	public static String truthtableToString(Expression e) {
		assert(e != null);
		String truthtable = "";

		char[] vars = Parser.getAllVariablesInOrder(e);
		String printingLine = String.valueOf(vars[0]);
		for (int i=1; i<vars.length; i++) {
			printingLine += "\t│\t" + vars[i];
		}
		printingLine += "\t│\t" + Parser.expressionToString(e);
		truthtable += printingLine + "\n";
		// next line in output
		printingLine = "";
		for (int i=0; i<50; i++) {
			printingLine += "─";
		}
		truthtable += printingLine + "\n";
		// next line in output
		// first line of boolean values -> everything is false
		boolean[][] booleanValues = binaryValues(vars.length);
		assert(booleanValues != null);
		for (int i=0; i<booleanValues.length; i++) {
			for (int j=0; j<booleanValues[i].length; j++) {
				int value = (booleanValues[i][j]) ? 1 : 0;
				truthtable += value + "\t";
				if (j < booleanValues[i].length-1) {
					truthtable += "\t";
				}
			}
			boolean ergebnisOfLine = Parser.parseExpressionToBoolean(e, vars, booleanValues[i]);
			int value = (ergebnisOfLine) ? 1 : 0;
			truthtable += "│\t" + value + "\n";
		}
		return truthtable;
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