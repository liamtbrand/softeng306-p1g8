package se306group8.scheduleoptimizer.dotfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class TokenTest {
	private PushbackReader createReader(String s) {
		ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		return new PushbackReader(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	@Test
	void testControlChars() throws IOException {
		PushbackReader reader = createReader("+;,+=");
		
		while(true) {
			Token token = Token.readNextToken(reader);
			
			if(token.type == Token.Type.EOF) {
				return;
			}
			
			assertEquals(token.type, Token.Type.CONTROL_CHAR);
		}
	}

	@Test
	void testBrackets() throws IOException {
		PushbackReader reader = createReader("[]{}");
		
		while(true) {
			Token token = Token.readNextToken(reader);
			
			if(token.type == Token.Type.EOF) {
				return;
			}
			
			assertEquals(token.type, Token.Type.BRACKET);
		}
	}
	
	@Test
	void testIDs() throws IOException {
		PushbackReader reader = createReader("stri g -0.982\r\n1234 -.5\n_ Aa_a1");
		
		assertEquals(Token.readNextToken(reader).getID(), "stri");
		assertEquals(Token.readNextToken(reader).getID(), "g");
		assertEquals(Token.readNextToken(reader).getID(), "-0.982");
		assertEquals(Token.readNextToken(reader).getID(), "1234");
		assertEquals(Token.readNextToken(reader).getID(), "-.5");
		assertEquals(Token.readNextToken(reader).getID(), "_");
		assertEquals(Token.readNextToken(reader).getID(), "Aa_a1");
	}
	
	@Test
	void testQuotes() throws IOException {
		PushbackReader reader = createReader(
				"\"\""
				+ "\"abc\""
				+ "\"graph\""
				+ "\"\\\n\""
				+ "\"\\\r\n\\apple \\\" \"");
		
		assertEquals(Token.readNextToken(reader).value, "");
		assertEquals(Token.readNextToken(reader).value, "abc");
		assertEquals(Token.readNextToken(reader).value, "graph");
		assertEquals(Token.readNextToken(reader).value, "");
		assertEquals(Token.readNextToken(reader).value, "\\apple \" ");
	}
	
	@Test
	void testIgnores() throws IOException {
		PushbackReader reader = createReader(
				"/* asdas */"
				+ "//\n"
				+ "// /* ** */ /*\r\n"
				+ "# abc abc abc\n"
				+ "//a\r\n"
				+ "strict");
		
		while(true) {
			Token token = Token.readNextToken(reader);
			
			if(token.type == Token.Type.EOF) {
				return;
			}
			
			assertEquals(token.type, Token.Type.IGNORED);
		}
	}
	
	@Test
	void testKeywords() throws IOException {
		PushbackReader reader = createReader("graph digraph node edge");
		
		while(true) {
			Token token = Token.readNextToken(reader);
			
			if(token.type == Token.Type.EOF) {
				return;
			}
			
			assertEquals(token.type, Token.Type.KEYWORD);
		}
	}
}
