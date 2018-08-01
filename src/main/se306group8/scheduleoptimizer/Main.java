package se306group8.scheduleoptimizer;

import se306group8.scheduleoptimizer.config.Config;

public class Main {
	
	public static void main(String[] args) {
		
		ArgsParser parser = new ArgsParser();
		Config config = parser.parse(args);
		
		if(config == null) {
			System.out.println("Something went wrong when parsing the config.");
			return;
		}

		System.out.println("inputFile = \""+config.inputFile()+"\"");
		System.out.println("P = " + config.P());
		System.out.println("N = " + config.N());
		System.out.println("visualize = "+config.visualize());
		System.out.println("outputFile = \""+config.outputFile()+"\"");
		
	}

}
