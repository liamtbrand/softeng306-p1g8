package se306group8.scheduleoptimizer;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se306group8.scheduleoptimizer.config.Config;
import se306group8.scheduleoptimizer.config.ConfigBuilder;

public class ArgsParser {
	
	private static final Logger LOGGER = Logger.getLogger( ArgsParser.class.getName() );
	
	public Config parse(String[] args) {
		
		Options options = new Options();
		
		options.addOption("p", true, "use N cores for execution in parallel (default is sequential)");
		options.addOption("v", "visualise the search");
		options.addOption("o", true, "output file is named OUTPUT (default is INPUT−output.dot)");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		
		// Setup defaults.
		
		ConfigBuilder builder = new ConfigBuilder();
		
		// Try to parse the arguments:
		
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			LOGGER.info("Unable to parse arguments.");
			LOGGER.severe(e.getStackTrace().toString());
			return null;
		}
		
		List<String> arguments = cmd.getArgList();
		
		// Make sure we have the correct number of arguments remaining.
		
		if(arguments.size() != 2) {
			LOGGER.info("Must pass arguments for parameters INPUT.dot and P.");
			return null;
		}
		
		// Get our input file name.
		
		String inputFile = arguments.get(0);
		if(!inputFile.substring(inputFile.length()-4, inputFile.length()).equals(".dot")) {
			LOGGER.info("Input file must be a .dot file.");
			return null;
		}
		builder.setInputFile(inputFile);
		
		// Get the number of processors, P.
		
		int P;
		try {
			P = Integer.parseInt(arguments.get(1));
		} catch (NumberFormatException e) {
			LOGGER.info("P must be an integer.");
			return null;
		}
		builder.setP(P);
		
		// Get the number of cores to use, N.
		
		if(cmd.hasOption("p")) {
			int N;
			try {
				N = Integer.parseInt(cmd.getOptionValue("p"));
			} catch (NumberFormatException e) {
				LOGGER.info("N must be an integer.");
				return null;
			}
			builder.setN(N);
		}
		
		builder.setVisualize(cmd.hasOption("v"));
		
		if(cmd.hasOption("o")) {
			builder.setOutputFile(cmd.getOptionValue("o"));
		}
		
		return builder.build();
		
	}

}
