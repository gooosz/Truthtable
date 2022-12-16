package hbrs.len.Datastructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node<T> {
	private T value;
	private ArrayList<Node<T>> children;
	public Node() {
		this.value = null;
		this.children = new ArrayList<>(0);
	}
	public Node(T value) {
		this.value = value;
		this.children = new ArrayList<>(0);
	}
	public Node(T value, int amountOfChildren) {
		this.value = value;
		this.children = new ArrayList<>(amountOfChildren);
	}
	public Node(T value, Node<T>... children) {
		this.value = value;
		this.children = new ArrayList<>(List.of(children));
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

	public ArrayList<Node<T>> getChildren() {
		return children;
	}
	/**
	 *
	 * @param n index
	 * @return n-th child aka n-th element in children ArrayList
	 */
	public Node<T> getChildren(int n) {
		return children.get(n);
	}
	public void addChildren(Node<T> child) {
		children.add(child);
	}

	/**
	 * @return true if no children
	 * false if children
	 */
	public boolean isEmpty() {
		return children.isEmpty();
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder(50);
		print(buffer, "", "");
		return buffer.toString();
	}

	private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
		buffer.append(prefix);
		buffer.append(this.getValue());
		buffer.append('\n');
		for (Iterator<Node<T>> it = children.iterator(); it.hasNext();) {
			Node<T> next = it.next();
			if (it.hasNext()) {
				next.print(buffer,
					childrenPrefix + "├── ",
					childrenPrefix + "│   ");
			} else {
				next.print(buffer,
					childrenPrefix + "└── ",
					childrenPrefix + "    ");
			}
		}
	}

}
