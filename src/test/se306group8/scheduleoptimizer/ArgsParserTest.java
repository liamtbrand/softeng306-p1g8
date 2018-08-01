package se306group8.scheduleoptimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se306group8.scheduleoptimizer.config.Config;

public class ArgsParserTest {
	
	private static ArgsParser parser;
	
	@BeforeAll
	static void setup() {
		parser = new ArgsParser();
	}
	
	@Test
	void testDefaultArgumentParsing() {
		
		String[] args = { "INPUT.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("INPUT.dot",config.inputFile());
		assertEquals(1,config.P());
		assertEquals(1,config.N());
		assertEquals(false,config.visualize());
		assertEquals("INPUT-output.dot",config.outputFile());
		
	}
	
	@Test
	void testCustomInputFile() {
		
		String[] args = { "myfile.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("myfile.dot",config.inputFile());
		assertEquals(1,config.P());
		assertEquals(1,config.N());
		assertEquals(false,config.visualize());
		assertEquals("myfile-output.dot",config.outputFile());
		
	}

}
