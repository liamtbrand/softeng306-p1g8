package se306group8.scheduleoptimizer.cli;

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
	void testMissingCompulsoryArgumentsIsInvalid() {
		
		String[] args = {};
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Must pass arguments for parameters INPUT.dot and P.",e.getMessage());
		}
		
		String[] args1 = { "myfile" };
		
		try {
			parser.parse(args1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Must pass arguments for parameters INPUT.dot and P.",e.getMessage());
		}
		
	}
	
	@Test
	void testDefaultArgumentParsing() throws ArgumentException {
		
		String[] args = { "INPUT.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("INPUT.dot",config.inputFile());
		assertEquals(1,config.processorsToScheduleOn());
		assertEquals(1,config.coresToUseForExecution());
		assertEquals(false,config.visualize());
		assertEquals("INPUT-output.dot",config.outputFile());
		
	}
	
	@Test
	void testCustomInputFile() throws ArgumentException {
		
		String[] args = { "myfile.dot", "1" };
		
		Config config = parser.parse(args);
		
		assertEquals("myfile.dot",config.inputFile());
		assertEquals(1,config.processorsToScheduleOn());
		assertEquals(1,config.coresToUseForExecution());
		assertEquals(false,config.visualize());
		assertEquals("myfile-output.dot",config.outputFile());
		
	}
	
	@Test
	void testInvalidFilename() {
		
		String[] args = { "somefile", "1" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Input file must be a .dot file.",e.getMessage());
		}
		
	}

	@Test
	void testNonIntegerPValueIsInvalid() {
		
		String[] args = { "infile.dot", "foo" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be an integer.",e.getMessage());
		}
		
	}
	
	@Test
	void testZeroOrLessPValueIsInvalid() {
		
		String[] args = { "infile.dot", "0" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
		
		String[] args1 = { "infile.dot", "-1" };
		
		try {
			parser.parse(args1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
		
	}
	
	@Test
	void testNonIntegerNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p", "foo" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be an integer.",e.getMessage());
		}
		
	}
	
	@Test
	void testZeroOrLessNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p", "0" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
		
		String[] args1 = { "infile.dot", "1", "-p", "-1" };
		
		try {
			parser.parse(args1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
		
	}
	
	@Test
	void testNoSuppliedNValueIsInvalid() {
		
		String[] args = { "infile.dot", "1", "-p" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Unable to parse malformed arguments.",e.getMessage());
		}
		
	}
	
	@Test
	void testVisualizationFlag() throws ArgumentException {
		
		String[] args = { "INPUT.dot", "1", "-v" };
		
		Config config = parser.parse(args);
		
		assertEquals("INPUT.dot",config.inputFile());
		assertEquals(1,config.processorsToScheduleOn());
		assertEquals(1,config.coresToUseForExecution());
		assertEquals(true,config.visualize());
		assertEquals("INPUT-output.dot",config.outputFile());
		
	}
	
	@Test
	void testCustomOutputFile() throws ArgumentException {
		
		String[] args = { "INPUT.dot", "1", "-o" };
		
		Config config;
		
		try {
			config = parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Unable to parse malformed arguments.",e.getMessage());
		}
		
		String[] args1 = { "infile.dot", "1", "-o", "outfile" };
		
		config = parser.parse(args1);
		
		assertEquals("infile.dot",config.inputFile());
		assertEquals(1,config.processorsToScheduleOn());
		assertEquals(1,config.coresToUseForExecution());
		assertEquals(false,config.visualize());
		assertEquals("outfile",config.outputFile());
		
	}
	
	@Test
	void testAdvancedConfigurationOne() throws ArgumentException {
		
		String[] args = { "advanced.dot", "5", "-p", "7", "-v" };
		
		Config config;
		
		config = parser.parse(args);
		
		assertEquals("advanced.dot",config.inputFile());
		assertEquals(5,config.processorsToScheduleOn());
		assertEquals(7,config.coresToUseForExecution());
		assertEquals(true,config.visualize());
		assertEquals("advanced-output.dot",config.outputFile());
		
	}
	
	@Test
	void testAdvancedConfigurationTwo() throws ArgumentException {
		
		String[] args = { "myadvanced.dot", "4", "-o", "myoutput.dot", "-p", "12", "-v" };
		
		Config config;
		
		config = parser.parse(args);
		
		assertEquals("myadvanced.dot",config.inputFile());
		assertEquals(4,config.processorsToScheduleOn());
		assertEquals(12,config.coresToUseForExecution());
		assertEquals(true,config.visualize());
		assertEquals("myoutput.dot",config.outputFile());
		
	}
	
	@Test
	void testInvalidAdvancedConfiguration() throws ArgumentException {
		
		String[] args = { "invalid.dot", "4", "-o", "-p", "12", "-v" };
		
		try {
			parser.parse(args);
			fail();
		} catch (ArgumentException e) {
			assertEquals("Unable to parse malformed arguments.",e.getMessage());
		}
		
	}
	
	
}
