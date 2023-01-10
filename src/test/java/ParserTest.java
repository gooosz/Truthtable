import hbrs.len.LogikModul.Expression;
import hbrs.len.Parser.Parser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
	private String expression;
	private Expression e;

	@Test
	public void testParserOnlyVariable() {
		expression = "a";
		e = Parser.parseExpression(expression);
		assertEquals("a\n", e.toString());
	}
	@Test
	public void testParserBracketOnlyVariable() {
		expression = "(a)";
		e = Parser.parseExpression(expression);
		assertEquals("a\n", e.toString());
	}
	@Test
	public void testParserNegationNoBrackets() {
		expression = "!a";
		e = Parser.parseExpression(expression);
		assertEquals("!\n" +
			"└── a",
			e.toString());
	}
	@Test
	public void testParserNegationInsideBracketsOnlyVariable() {
		expression = "(!a)";
		e = Parser.parseExpression(expression);
		assertEquals("!\n" +
			"└── a", e.toString());
	}
	@Test
	public void testParserNegationOutsideBracketsOnlyVariable() {
		expression = "!(a)";
		e = Parser.parseExpression(expression);
		assertEquals("!\n" +
			"└── a", e.toString());
	}

	@Test
	public void testParserExpressionTreeToString() {
		String formel = "a&b";
		Expression e = Parser.parseExpression(formel);
		String formelFromExpression = Parser.expressionToString(e);
		assertEquals("(" + formel + ")", formelFromExpression);
	}
}
