package se306group8.scheduleoptimizer;

import se306group8.scheduleoptimizer.cli.ArgsParser;
import se306group8.scheduleoptimizer.config.ArgumentException;
import se306group8.scheduleoptimizer.config.Config;

public class Main {
	
	public static void main(String[] args) {
		
		ArgsParser parser = new ArgsParser();
		Config config;
		
		try {
			config = parser.parse(args);
		} catch (ArgumentException e) {
			System.out.println("Problem parsing arguments: "+e.getMessage());
			return; // Stop prematurely.
		}

		System.out.println("inputFile = \""+config.inputFile()+"\"");
		System.out.println("P = " + config.P());
		System.out.println("N = " + config.N());
		System.out.println("visualize = "+config.visualize());
		System.out.println("outputFile = \""+config.outputFile()+"\"");
		
	}

}
