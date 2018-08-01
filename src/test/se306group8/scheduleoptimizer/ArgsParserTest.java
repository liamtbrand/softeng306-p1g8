package se306group8.scheduleoptimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.config.ArgumentException;
import se306group8.scheduleoptimizer.config.Config;

public class ArgsParserTest {
	
	private static ArgsParser parser;
	
	@BeforeAll
	static void setup() {
		parser = new ArgsParser();
	}
	
	@Test
	void testDefaultArgumentParsing() throws ArgumentException {
		
		String[] args = { "INPUT.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("INPUT.dot",config.inputFile());
		assertEquals(1,config.P());
		assertEquals(1,config.N());
		assertEquals(false,config.visualize());
		assertEquals("INPUT-output.dot",config.outputFile());
		
	}
	
	@Test
	void testCustomInputFile() throws ArgumentException {
		
		String[] args = { "myfile.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("myfile.dot",config.inputFile());
		assertEquals(1,config.P());
		assertEquals(1,config.N());
		assertEquals(false,config.visualize());
		assertEquals("myfile-output.dot",config.outputFile());
		
	}
	
	@Test
	void testInvalidFilename() {
		
		String[] args = { "somefile", "1" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Input file must be a .dot file.",e.getMessage());
		}
		
	}

	@Test
	void testNonIntegerPValueIsInvalid() {
		
		String[] args = { "infile.dot", "foo" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be an integer.",e.getMessage());
		}
		
	}
	
	@Test
	void testZeroOrLessPValueIsInvalid() {
		
		String[] args = { "infile.dot", "0" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
		
		String[] args1 = { "infile.dot", "-1" };
		
		try {
			Config config = parser.parse(args1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
		
	}
	
	@Test
	void testNonIntegerNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p", "foo" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be an integer.",e.getMessage());
		}
		
	}
	
	@Test
	void testZeroOrLessNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p", "0" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
		
		String[] args1 = { "infile.dot", "1", "-p", "-1" };
		
		try {
			Config config = parser.parse(args1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
		
	}
	
	@Test
	void testNoSuppliedNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p" };
		
		try {
			Config config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Unable to parse malformed arguments.",e.getMessage());
		}
		
	}
	
	
	
}
