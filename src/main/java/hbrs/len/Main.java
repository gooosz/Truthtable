package hbrs.len;

import hbrs.len.Datastructures.AST;
import hbrs.len.Datastructures.Node;
import hbrs.len.Parser.Parser;

public class Main {
	public static void main(String[] args) {
		testParseAST();
	}

	public static void testAST() {
		AST<Character> ast = new AST<>('a');
		AST<Character> bst = new AST<>('1');
		AST<Character> cst = new AST<>('2');

		ast.add(bst);
		bst.add(cst);

		System.out.println(ast.toString());
	}

	public static void testParseOperator() {
		String f = "(a&b)";
		System.out.println();
	}

	public static void testClosingBrackets() {
		String f = "(a|b)&c";
		System.out.println(Parser.getClosingBracketIndexToOpenBracket(f, 0));
	}
	public static void testOpeningBrackets() {
		String f = "a&(a|!c)&b";
		System.out.println(Parser.getFirstOpeningBracketsOccurence(f));
	}

	public static void testParseAST() {
		String f = "(((a|b)&c)>(d&a))";
		AST<Character> r = new AST<>();
		Parser.parseIntoAST(f, r);
		assert(r != null);
		System.out.println(r.toString());
	}
}