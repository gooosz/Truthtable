/**
 * AST is a Abstract Syntax Tree
 *
 * It is not allowed to construct a AST with initial children
 * -> for several good reasons
 */

package hbrs.len.Datastructures;

import java.util.Iterator;

public class AST<T> {
	private Node<T> root;

	public AST() {
		root = new Node<>();
	}
	public AST(T nodeValue) {
		root = new Node<>(nodeValue);
	}
	public AST(Node<T> root) {
		this.root = root;
	}

	public AST(AST<T> root) {
		this.root = root.getRoot();
	}

	public Node<T> getRoot() {
		return root;
	}
	public void setRoot(Node<T> root) {
		this.root = root;
	}

	/**
	 *
	 * @param children new children of root
	 * @return root
	 */
	public Node<T> add(Node<T>... children) {
		assert(children.length > 0);
		for (Node<T> child: children) {
			root.addChildren(child);
		}
		return root;
	}

	/**
	 *
	 * @param tree only tree.root is this.root direct child
	 * @return this.root
	 */
	public Node<T> add(AST<T> tree) {
		assert(tree != null);
		root.addChildren(tree.getRoot());
		return root;
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
