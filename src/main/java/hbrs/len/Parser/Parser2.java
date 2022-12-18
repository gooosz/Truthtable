package hbrs.len.Parser;

import com.sun.security.jgss.GSSUtil;
import hbrs.len.LogikModul.Expression;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Parser2 {
	private static final char[] operators = {'&', '|', '>', '!'};
	public static Expression parseExpression(String expression) {
		if (expression == null || expression.length() == 0) {
			return null;
		}
		if (expression.length() == 1) {
			return new Expression(expression.charAt(0));
		}

		/*
		 * Get all root operator indizes
		*/
		ArrayList<Integer> rootOperatorIndizes = getRootOperatorIndexe(expression);
		if (rootOperatorIndizes == null) {
			// Strip brackets
			for (int i=0; i<expression.length(); i++) {
				char currentChar = expression.charAt(i);
				if (currentChar != '(' && currentChar != ')') {
					return new Expression(currentChar);
				}
			}
			System.out.println("Error no variable!");
			return null;
		}

		// Construct root
		if (rootOperatorIndizes.size() > 1) {
			// Still TODO
			/*
			 * (a&b)|(c&d)|(e&f)
			 * or
			 * (a&b)|(c&d)|(e&f)|(g&h)
			 * etc
			*/

			// root operators must be the same!!!
			for (int k=0; k<rootOperatorIndizes.size()-1; k++) {
				int indexOperator1 = rootOperatorIndizes.get(k);
				int indexOperator2 = rootOperatorIndizes.get(k+1);
				// if they are not equal, use left to right
				// by applying brackets around the equal ones
				if (expression.charAt(indexOperator1) != expression.charAt(indexOperator2)) {
					String expressionLeftToRight = "("
						+ expression.substring(0, indexOperator2)
						+ ")"
						+ expression.substring(indexOperator2);
					return parseExpression(expressionLeftToRight);
				}
			}
			char rootOperator = expression.charAt(rootOperatorIndizes.get(0));
			Expression root = new Expression(rootOperator);
			/* there is always one child more than amount of rootOperators
			 * startStopSubExpression[0] inclusive
			 * startStopSubExpression[0] exclusive
			*/
			int[] startStopSubExpression = {0, rootOperatorIndizes.get(0)};
			for (int i=0; i<rootOperatorIndizes.size()+1; i++) {
				/*
				 * start of subexpression
				*/
				String subexpression = expression.substring(startStopSubExpression[0],
										startStopSubExpression[1]);
				Expression child = parseExpression(subexpression);
				root.add(child);

				// update to next subexpression index range values
				if (i >= rootOperatorIndizes.size()) {
					startStopSubExpression[0] = 0;
				} else {
					startStopSubExpression[0] = rootOperatorIndizes.get(i)+1;
				}
				// after last operator, the end is the end
				if (i >= rootOperatorIndizes.size()-1) {
					startStopSubExpression[1] = expression.length();
				} else {
					startStopSubExpression[1] = rootOperatorIndizes.get(i+1);
				}
			}
			// Still TODO
			return root;
		}

		rootOperatorIndizes.subList(1, rootOperatorIndizes.size()).clear();
		// only one root operator
		char rootOperator = expression.charAt(rootOperatorIndizes.get(0));
		Expression root = new Expression(rootOperator);
		/*
		 * Construct left and right child
		 *
		 * Construct only the right child if root operator is '!'
		*/
		if (rootOperator != '!') {
			// Construct left child
			String leftSubexpression = expression.substring(0,
								rootOperatorIndizes.get(0));
			Expression leftChild = parseExpression(leftSubexpression);
			if (leftChild != null) {
				root.add(leftChild);
			}
		}
		// Construct right child
		String rightSubexpression = expression.substring(rootOperatorIndizes.get(0)+1);
		Expression rightChild = parseExpression(rightSubexpression);
		if (rightChild != null) {
			root.add(rightChild);
		}
		return root;
	}

	/**
	 * @param expression looks like this:
	 *        a&b
	 *        (a&b)|c
	 *        (a&b)|(c&d)
	 * @return the top-level operator
	 */
	public static ArrayList<Integer> getRootOperatorIndexe(String expression) {
		if (expression == null || expression.length() == 0 || expression.length() == 1) {
			return null;
		}

		// all root operators
		ArrayList<Integer> rootOperatorsIndexe = getOperatorIndexOutOfBrackets(expression);
		if (rootOperatorsIndexe == null || rootOperatorsIndexe.size() == 0) {
			/*
			 * Strip expression
			 * so
			 * (a&b)
			 * turns into
			 * a&b
			*/
			String expressionWithoutOuterBrackets
				= expression.substring(1, expression.length()-1);
			ArrayList<Integer> rootOperatorIndizes
				= getRootOperatorIndexe(expressionWithoutOuterBrackets);
			/*
			 * If rootOperatorIndizes is still zero -> empty expression so return
			*/
			if (rootOperatorIndizes == null) {
				return null;
			}
			/*
			 * Outer brackets where removed so every position is
			 * the actual position -1
			 * -> so add 1 to every position
			 */
			rootOperatorIndizes.replaceAll(index -> index + 1);
			/*
			 * remove ! if another operator is present
			 */
			assert(rootOperatorsIndexe != null);
			removeNegationIfNotRoot(expression, rootOperatorsIndexe);
			return rootOperatorIndizes;
		}
		/*
		 * remove ! if another operator is present
		 */
		removeNegationIfNotRoot(expression, rootOperatorsIndexe);
		return rootOperatorsIndexe;
	}

	public static void removeNegationIfNotRoot(String expression, ArrayList<Integer> indexOfRootOperators) {
		if (indexOfRootOperators == null) {
			return;
		}
		for (int i=0; i<indexOfRootOperators.size(); i++) {
			/*
			 * Remove the negation ONLY if no other operator is present
			 */
			int indexOfOperator = indexOfRootOperators.get(i);
			if (expression.charAt(indexOfOperator) == '!'
				&& indexOfRootOperators.size() > 1)
			{
				indexOfRootOperators.remove(i);
				i--;
			}
		}
	}

	/**
	 * @param expression expression
	 * @return ArrayList of positions of all operators that are outside of brackets
	 * so
	 * (a&b)|c returns position of |
	 * (a&b) returns null
	 * a&b returns position of &
	 */
	public static ArrayList<Integer> getOperatorIndexOutOfBrackets(String expression) {
		ArrayList<Integer> positionsOfOperators = new ArrayList<>();
		int[] outerBrackets = getOuterBracketIndexe(expression);
		// (a&b)
		if (outerBrackets[0] == 0 && outerBrackets[1] == expression.length()-1) {
			return null;
		}

		// search for operator before brackets
		int bracketCounter = 0;
		for (int i=0; i<expression.length(); i++) {
			// ignore everything that is in brackets
			if (expression.charAt(i) == '(') {
				bracketCounter++;
			}
			if (expression.charAt(i) == ')') {
				bracketCounter--;
			}
			if (isOperator(expression.charAt(i)) && bracketCounter == 0) {
				// operator is outside any brackets
				positionsOfOperators.add(i);
			}
		}
		return positionsOfOperators;
	}

	private static int[] getOuterBracketIndexe(String expression) {
		int openBracketIndex = getNextBracketIndex(expression);
		int closeBracketIndex = getClosingBracketIndexFromOpenBracket(expression,
						openBracketIndex);
		return new int[]{openBracketIndex, closeBracketIndex};
	}

	private static boolean isNegated(String expression) {
		if (expression == null || expression.length() == 0) {
			return false;
		}
		return (expression.charAt(0) == '!');
	}

	/**
	 *
	 * @return Expression that has the operator c as
	 */
	private static Expression passOperatorAsRootOperator(String expression,
							     char rootOperator,
							     int indexOfRootOperator,
							     boolean hasLeftSubexpression,
							     boolean hasRightSubexpression) {
		Expression root = new Expression(rootOperator);

		if (hasLeftSubexpression) {
			String leftSubexpression = expression.substring(0, indexOfRootOperator);
			root.add(parseExpression(leftSubexpression));
		}

		if (hasRightSubexpression) {
			String rightSubexpression = expression.substring(indexOfRootOperator+1);
			root.add(parseExpression(rightSubexpression));
		}
		return root;
	}


	private static int getNextBracketIndex(String expression) {
		for (int i=0; i<expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				return i;
			}
		}
		// No bracket found
		return -1;
	}
	private static
	int getClosingBracketIndexFromOpenBracket(String expression,
						  int openBracketIndex) {
		assert(openBracketIndex > 0 && openBracketIndex < expression.length());
		assert(expression.charAt(openBracketIndex) == '(');
		/*
		 * expression may be: a(b&(c&))
		 * -> e.g. openBracketIndex: 1
		 * -> closingBracketIndex = 8
		 * innerBracketCount = 1
		 */
		int indexOfClosingBracket = -1;
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

	private static char getNextOperator(String expression) {
		for (int i=0; i<expression.length(); i++) {
			char currentChar = expression.charAt(i);
			if (isOperator(currentChar)) {
				return currentChar;
			}
		}
		// No next operator
		return 0;
	}

	private static boolean isOperator(char c) {
		for (int i=0; i<operators.length; i++) {
			if (c == operators[i]) {
				return true;
			}
		}
		return false;
	}

	private static ArrayList<Integer> getAllOperatorIndexInExpression(String expression) {
		ArrayList<Integer> indexe = new ArrayList<>();
		for (int i=0; i<expression.length(); i++) {
			if (isOperator(expression.charAt(i))) {
				indexe.add(i);
			}
		}
		return indexe;
	}


}
