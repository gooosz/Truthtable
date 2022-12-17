package hbrs.len.Parser;


import hbrs.len.Datastructures.AST;
import hbrs.len.Datastructures.Node;

import java.util.ArrayList;

public class Parser {
	private static String expression = "";
	/*
	 * &: Conjunction
	 * |: Disjunction
	 * >: Subjunction
	 * !: Negation
	 * with brackets
	 */
	public static final char[] operators = {'&', '|', '>', '!', '(', ')'};


	public static String getExpression() {
		return expression;
	}
	public static void setExpression(String newExpression) {
		expression = newExpression;
	}

	/**
	 *
	 * @param expression formel
	 * @param astOfVar expression as a AST, may be null at first
	 * parses expression and creates an Abstract Syntax Tree
	 * out of it
	 */
	public static void parseIntoAST(String expression, AST<Character> astOfVar) {
		if (expression == null || expression.equals("")) {
			return;
		}
		// just an operator
		if (expression.length() == 1) {
			astOfVar.setRoot(new Node<>(expression.charAt(0)));
			return;
		}

		int operatorenCounterIndex = 0;
		if (getNextOperator(expression, operatorenCounterIndex) == '!') {
			astOfVar.setRoot(new Node<>('!'));
			int indexOfNegation = expression.indexOf('!');
			assert(indexOfNegation >= 0);
			String subexpression = expression.substring(indexOfNegation+1);
			AST<Character> negationFormel = new AST<>();
			parseIntoAST(subexpression, negationFormel);
			astOfVar.add(negationFormel);
			return;
		}

		ArrayList<Integer> posOfOperatorsInExpression = new ArrayList<>(0);
		// fill positionsOfOperators with the positions of operators in expression
		for (int i=0; i<expression.length(); i++) {
			/*
			 * generally: next operator is at index i+2
			 * special case: operator = '!' may be after every other operator
			 * 	or at the very beginning of expression
			 *
			*/
			char nextOperator = getNextOperator(expression, i);
			if (nextOperator == ' ') {
				/*
				 * no more operator in expression
				 * finished parsing all operators
				 */
				break;
			}
			posOfOperatorsInExpression.add(i);
		}

		//astOfVar = new AST<>();
		/*
		 * Add all operators to astOfVar
		 *
		 * Nodes with children: operator
		 * Nodes without children: variable
		 *
		 * Variables are between the positionsOfOperators
		 *
		 * !!! brackets may occur !!!
		*/

		/*
		 * 1:
		 *
		 * search for the outmost brackets
		 */
		int firstBracketsIndex = getFirstOpeningBracketsOccurence(expression);
		if (firstBracketsIndex == 0) {
			firstBracketsIndex = getFirstOpeningBracketsOccurence(
						expression.substring(1));
		}
		/*
		 * The expression "(a&b)"
		 * will now be looked at like it was
		 * "a&b"
		*/
		int lastBracketsIndex = getClosingBracketIndexToOpenBracket
						(expression, firstBracketsIndex);

		/*
		 * check if there exists something like
		 * "(a&b)&c"
		 * 	 ↑
		 */
		int indexOfOperatorAfterBrackets = lastBracketsIndex + 1;
		if (indexOfOperatorAfterBrackets >= expression.length()) {
			// root is in the brackets
			String subexpression = expression.substring(firstBracketsIndex+1,
									lastBracketsIndex);
			parseIntoAST(subexpression, astOfVar);
			return;
		}

		/*
		 * check for negation
		 * negation might be "!!!!!!!!!(expression)"
		 * so go left one more to get to the root operator
		*/
		char rootOperator = expression.charAt(indexOfOperatorAfterBrackets);
		astOfVar.setRoot(new Node<>(rootOperator));



		// ↑ Works ↑ !


		/*
		 * 2:
		 *
		 * Construct the leftChild AST from root
		 * so everything left from rootOperator
		*/
		//int leftSubexpressionEndIndex = indexOfOperatorAfterBrackets - 1;
		String leftSubexpression = expression.substring(0, indexOfOperatorAfterBrackets);
		AST<Character> left = new AST<>();
		parseIntoAST(leftSubexpression, left);
		astOfVar.add(left);

		/*
		 * 2:
		 *
		 * Construct the rightChild AST from root
		 * so everything right from rootOperator
		*/
		String rightSubexpression = expression.substring(indexOfOperatorAfterBrackets+1);
		AST<Character> right = new AST<>();
		parseIntoAST(rightSubexpression, right);
		astOfVar.add(right);
	}

	/**
	 *
	 * @param expression expression
	 * @param openBracketIndex index of opening bracket
	 * @return index of closing bracket associated to openBracket
	 */
	private static
	int getClosingBracketIndexToOpenBracket(String expression, int openBracketIndex) {
		assert(expression.charAt(openBracketIndex) == '(');
		/*
		 * expression may be: a(b&(c&))
		 * -> e.g. openBracketIndex: 1
		 * -> closingBracketIndex = 8
		 * innerBracketCount = 1
		 */
		int indexOfClosingBracket = 0;
		int innerBracketCount = 0;
		for (int i=openBracketIndex+1; i<expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				innerBracketCount++;
			} else if (expression.charAt(i) == ')') {
				if (innerBracketCount == 0) {
					// closing bracket found
					indexOfClosingBracket = i;
					break;
				} else {
					innerBracketCount--;
				}
			}
		}
		return indexOfClosingBracket;
	}

	/**
	 * @param expression expression
	 * @return is the index of first opening bracket
	 * if no opening bracket: return -1
	 */
	private static int getFirstOpeningBracketsOccurence(String expression) {
		int firstOpeningBracketIndex = 0;
		for (int i=0; i<expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				firstOpeningBracketIndex = i;
				break;
			}
		}
		return firstOpeningBracketIndex;
	}
	/**
	 * @param expression expression
	 * @return is the index of root operator + 1
	 */
	private static int getLastOpeningBracketsOccurence(String expression) {
		int lastOpeningBracketIndex = 0;
		for (int i=0; i<expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				lastOpeningBracketIndex = i;
			}
		}
		return lastOpeningBracketIndex;
	}

	/**
	 * @param expression to be parsed
	 * @param index searching from index as start
	 * @return the next operator
	 */
	private static char getNextOperator(String expression, int index) {
		assert(index < expression.length());
		char currentChar = expression.charAt(index);
		int operatorIndex = whichOperator(currentChar, operators);
		if (operatorIndex == -1) {
			// Not an operator
			// Check if expression isn't at end yet
			if (index < expression.length()-1) {
				return getNextOperator(expression, index+1);
			} else {
				// No next operator found
				return ' ';
			}
		}
		return operators[operatorIndex];
	}

	/**
	 *
	 * @param c
	 * @param operators all allowed operators
	 * @return the position of c in operators
	 * -1 if not in operators
	 */
	private static int whichOperator(char c, char[] operators) {
		for (int i=0; i<operators.length; i++) {
			if (operators[i] == c) {
				return i;
			}
		}
		return -1;
	}

	public static  boolean evaluate(AST<Character> vars) {
		return false;
	}
}
