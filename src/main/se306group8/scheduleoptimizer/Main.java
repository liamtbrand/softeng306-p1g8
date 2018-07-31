package se306group8.scheduleoptimizer;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {

		Options options = new Options();
		
		options.addOption("p", true, "use N cores for execution in parallel (default is sequential)");
		options.addOption("v", "visualise the search");
		options.addOption("o", true, "output file is named OUTPUT (default is INPUT−output.dot)");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		
		// Setup defaults.
		
		String inputFile = "INPUT.dot";			// INPUT file.
		int P = 1;								// Number of processors.
		int N = 1;								// Number of cores for parallel execution.
		boolean visualize = false;				// Enable visualization.
		String outputFile = "INPUT-output.dot";	// Output file.
		
		// Try to parse the arguments:
		
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		List<String> arguments = cmd.getArgList();
		
		// Make sure we have the correct number of arguments remaining.
		
		if(arguments.size() != 2) {
			// TODO log this.
			System.out.println("Must pass arguments for parameters INPUT.dot and P.");
			return;
		}
		
		// Get our input file name.
		
		inputFile = arguments.get(0);
		
		// Get the number of processors, P.
		
		try {
			P = Integer.parseInt(arguments.get(1));
		} catch (NumberFormatException e) {
			// TODO log this.
			System.out.println("P must be an integer.");
			return;
		}
		
		// Get the number of cores to use, N.
		
		if(cmd.hasOption("p")) {
			try {
				N = Integer.parseInt(cmd.getOptionValue("p"));
			} catch (NumberFormatException e) {
				// TODO log this.
				System.out.println("N must be an integer.");
				return;
			}
		}
		
		visualize = cmd.hasOption("v");
		
		if(cmd.hasOption("o")) {
			outputFile = cmd.getOptionValue("o");
		} else {
			outputFile = inputFile.substring(0, inputFile.length()-4)+"-output.dot";
		}
		
		// TODO begin the algorithm.
		
		
		
	}

}
