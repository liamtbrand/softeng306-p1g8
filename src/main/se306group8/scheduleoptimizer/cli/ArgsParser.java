package se306group8.scheduleoptimizer.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se306group8.scheduleoptimizer.config.Config;
import se306group8.scheduleoptimizer.config.ConfigBuilder;
import se306group8.scheduleoptimizer.config.ArgumentException;

/**
 * 
 * A parser to parse the CLI arguments into a Config object.
 *
 */
public class ArgsParser {
	
	/**
	 * Calling parse will return a Config object which represents the configuration to be used for this search.
	 * If an ArgumentException is thrown, the Config object was unable to be created successfully.
	 * 
	 * @param args - The CLI arguments. These should be passed in from main(String[] args).
	 * @return Config - An object representing the configuration for running the search.
	 * @throws ArgumentException - An exception indicating that a Config object could not be created. The search should be aborted.
	 */
	public Config parse(String[] args) throws ArgumentException {
		
		/*
		 * Arguments will come in the format:
		 * 
		 * java −jar scheduler.jar INPUT.dot P [OPTION]
		 * INPUT.dot	a task graph with integer weights in dot format
		 * P			number of processors to schedule the INPUT graph on
		 * 
		 * -p N			use N cores for execution in parallel (default is sequential)
		 * -v			visualize the search
		 * -o OUTPUT	output file is named OUTPUT (default is INPUT−output.dot)
		 */
		
		Options options = new Options();
		
		options.addOption("p", true, "use N cores for execution in parallel (default is sequential)");
		options.addOption("v", "visualise the search");
		options.addOption("o", true, "output file is named OUTPUT (default is INPUT−output.dot)");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		
		// Setup defaults.
		
		ConfigBuilder builder = new ConfigBuilder();
		
		// Grab the compulsory arguments.
		
		if(args.length < 2) {
			throw new ArgumentException("Must pass arguments for parameters INPUT.dot and P.");
		}
		
		String[] argsToParse = new String[args.length-2];
		
		for(int i = 0; i < argsToParse.length; i++) {
			argsToParse[i] = args[i+2];
		}
		
		// Try to parse the arguments:
		
		try {
			cmd = parser.parse(options, argsToParse);
		} catch (ParseException e) {
			throw new ArgumentException("Unable to parse malformed arguments.");
		}
		
		// Make sure we have the correct number of arguments remaining.
		
		if(cmd.getArgList().size() != 0) {
			throw new ArgumentException("Unknown arguments.");
		}
		
		// Get our input file name.
		
		builder.setInputFile(args[0]);
		
		// Get the number of processors, P.
		
		try {
			builder.setP(Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			throw new ArgumentException("P must be an integer.");
		}
		
		// Get the number of cores to use, N.
		
		if(cmd.hasOption("p")) {
			try {
				builder.setN(Integer.parseInt(cmd.getOptionValue("p")));
			} catch (NumberFormatException e) {
				throw new ArgumentException("N must be an integer.");
			}
		}
		
		builder.setVisualize(cmd.hasOption("v"));
		
		if(cmd.hasOption("o")) {
			builder.setOutputFile(cmd.getOptionValue("o"));
		}
		
		return builder.build();
		
	}

}
