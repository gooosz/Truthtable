import hbrs.len.LogikModul.Expression;
import hbrs.len.Parser.Parser2;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
	private String expression;
	private Expression e;

	@Test
	public void testParserOnlyVariable() {
		expression = "a";
		e = Parser2.parseExpression(expression);
		assertEquals("a", e.toString());
	}
	@Test
	public void testParserBracketOnlyVariable() {
		expression = "(a)";
		e = Parser2.parseExpression(expression);
		assertEquals("a", e.toString());
	}
	@Test
	public void testParserNegationNoBrackets() {
		expression = "!a";
		e = Parser2.parseExpression(expression);
		assertEquals("!\n" +
			"└── a",
			e.toString());
	}
	@Test
	public void testParserNegationInsideBracketsOnlyVariable() {
		expression = "(!a)";
		e = Parser2.parseExpression(expression);
		assertEquals("!\n" +
			"└── a", e.toString());
	}
	@Test
	public void testParserNegationOutsideBracketsOnlyVariable() {
		expression = "!(a)";
		e = Parser2.parseExpression(expression);
		assertEquals("!\n" +
			"└── a", e.toString());
	}
}
