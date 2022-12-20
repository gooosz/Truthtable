package hbrs.len.LogikModul;

import hbrs.len.Datastructures.AST;
import hbrs.len.Datastructures.Node;

import java.util.ArrayList;

public class Expression extends AST<Character> {
	public Expression() {
		super();
	}
	public Expression(char c) {
		super(c);
	}
	public Expression(Node<Character> root) {
		super(root);
	}
	public Expression(Expression root) {
		super(root);
	}
}
