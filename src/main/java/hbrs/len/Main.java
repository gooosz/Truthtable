package hbrs.len;

import hbrs.len.Datastructures.AST;
import hbrs.len.LogikModul.Expression;
import hbrs.len.Parser.Parser;

public class Main {
	public static void main(String[] args) {
		testParser2();
	}

	public static void testParser2() {
		String f = "!(a&b)|!c|a";
		Expression e = Parser.parseExpression(f);
		System.out.println(e);
	}

	public static void testAST() {
		AST<Character> ast = new AST<>('a');
		AST<Character> bst = new AST<>('1');
		AST<Character> cst = new AST<>('2');

		ast.add(bst);
		bst.add(cst);

		System.out.println(ast.toString());
	}

	public static void testParseAST() {
		//String f = "(a>b)&(d|e)";
		//String f = "((a|b)&c)";
		String f = "!(a|b)&c|!d";
		AST<Character> r = Parser.parseExpression(f);
		System.out.println(r.toString());
	}
}