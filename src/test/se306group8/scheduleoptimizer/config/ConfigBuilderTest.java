package se306group8.scheduleoptimizer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigBuilderTest {
	
	private static ConfigBuilder builder;
	
	@BeforeEach
	void setup() {
		builder = new ConfigBuilder();
	}
	
	@Test
	void testConfigBuilderCustomInputFile() throws ArgumentException {
		
		builder.setInputFile("MyFile.dot");
		
		Config config = builder.build();
		
		assertEquals("MyFile.dot",config.inputFile());
		assertEquals("MyFile-output.dot",config.outputFile());
		
	}
	
	@Test
	void testConfigBuilderOverwritesAndResets() throws ArgumentException {
		
		builder.setInputFile("hello.dot");
		builder.setVisualize(true);
		builder.setP(5);
		builder.setVisualize(false);
		builder.setN(2);
		builder.setInputFile("hi.dot");
		builder.setOutputFile("myoutput");
		builder.setN(6);
		builder.setVisualize(true);
		builder.setInputFile("myinput.dot");
		
		Config config = builder.build();
		
		assertEquals("myinput.dot", config.inputFile());
		assertEquals(6, config.N());
		assertEquals(5, config.P());
		assertEquals(true, config.visualize());
		assertEquals("myoutput", config.outputFile());
		
		config = builder.build();
		
		assertEquals("INPUT.dot", config.inputFile());
		assertEquals(1, config.N());
		assertEquals(1, config.P());
		assertEquals(false, config.visualize());
		assertEquals("INPUT-output.dot", config.outputFile());
		
	}
	
	@Test
	void testInvalidInputFile() {
		
		try {
			builder.setInputFile("invalid");
			builder.build();
			fail();
		} catch (ArgumentException e) {
			assertEquals("Input file must be a .dot file.",e.getMessage());
		}
		
	}
	
	@Test
	void testInvalidN() {
		try {
			builder.setN(-1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
		
		try {
			builder.setN(0);
			fail();
		} catch (ArgumentException e) {
			assertEquals("N must be a +ve integer value.",e.getMessage());
		}
	}
	
	@Test
	void testInvalidP() {
		try {
			builder.setP(-1);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
		
		try {
			builder.setP(0);
			fail();
		} catch (ArgumentException e) {
			assertEquals("P must be a +ve integer value.",e.getMessage());
		}
	}

}
