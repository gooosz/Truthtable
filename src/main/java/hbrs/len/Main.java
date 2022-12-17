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

	public static void testParseAST() {
		//String f = "((a>b)&(d|e))";
		//String f = "((a|b)&c)";
		String f = "!((a&b)|!c)";
		AST<Character> r = new AST<>();
		Parser.parseIntoAST(f, r);
		System.out.println(r.toString());
	}
}