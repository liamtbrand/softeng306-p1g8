package se306group8.scheduleoptimizer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConfigTest {

	@Test
	void testDefaultConfigIsCorrect() {
		
		ConfigBuilder builder = new ConfigBuilder();
		Config config = builder.build();
		
		assertEquals("INPUT.dot",config.inputFile());
		assertEquals(1,config.P());
		assertEquals(1,config.N());
		assertEquals(false,config.visualize());
		assertEquals("INPUT-output.dot",config.outputFile());
		
	}
	
}
